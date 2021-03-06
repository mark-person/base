package com.ppx.cloud.monitor.queue;

import java.util.BitSet;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.monitor.config.MonitorSwitchConfig;
import com.ppx.cloud.monitor.config.MonitorThresholdProperties;
import com.ppx.cloud.monitor.output.ConsoleImpl;
import com.ppx.cloud.monitor.output.PersistenceImpl;
import com.ppx.cloud.monitor.pojo.AccessLog;
import com.ppx.cloud.monitor.util.AccessAnalysisUtils;

/**
 * 队列消费者,用于日志输出
 * 
 * @author mark
 * @date 2018年6月16日
 */
@Component
public class AccessQueueConsumer {

	private static final Logger logger = LoggerFactory.getLogger(AccessQueueConsumer.class);

	public void start() {
		// 启动后调用
		logger.info("AccessQueueConsumer.start()...");

		new Thread(() -> {
			// 加上try内防止线程死掉
			try {
				// 定时采集,刚开机cpu占100%
				TimeUnit.SECONDS.sleep(10);
				TimingGather.gather();
				
				while (true) {
					consumeAccessLog();
					
					// 间隔
					intervalRun();
					Thread.sleep(20);
				}
			} catch (Throwable e) {
				logger.error("AccessQueueConsumer.Thread", e);
			}
		}).start();
	}

	private void consumeAccessLog() {
		AccessLog a;
		while ((a = AccessQueue.getQueue().poll()) != null) {
			if (MonitorSwitchConfig.IS_DEV) {
				ConsoleImpl.print(a);
			}
			
			// 监控页面的查看不输出(异常时输出)
			if (a.getUriSeq() != null && a.getUriSeq() == -1 && a.getThrowable() == null) {
				continue;
			}
			
			try {
				logToDb(a);
			} catch (Throwable e) {
				e.printStackTrace();
				// 输出异常，则打印到控制台
				logger.error("Error(logToDb):{}", e.getMessage());
				if (!MonitorSwitchConfig.IS_DEV) {
					ConsoleImpl.print(a);
				}
			}
		}
	}

	private long lastGatherNanoTime = System.nanoTime();

	private void intervalRun() {
		// 采集间隔
		long currentNanoTime = System.nanoTime();
		if (currentNanoTime - lastGatherNanoTime >= MonitorThresholdProperties.GATHER_INTERVAL * 1e6) {
			lastGatherNanoTime = currentNanoTime;
			TimingGather.gather();
		}
	}

	private void logToDb(AccessLog a) {

		// long t1 = System.currentTimeMillis();


		// 访问日志、 访问日志索引、uri统计、sql统计、响应统计
		try (LogTemplate t = new LogTemplate()) {
			PersistenceImpl impl = PersistenceImpl.getInstance(t);
			int accessId = impl.insertAccess(a);
			impl.insertStatUri(a);
			impl.insertStatSql(a);

			if (MonitorSwitchConfig.IS_DEBUG) {
				// debug日志
				impl.insertDebug(accessId, a);
			}

			if (a.getThrowable() == null) {
				// 响应时间统计(有异常的不统计响应时间)
				impl.insertResponse(a);
			} else {
				// 异常处理
				impl.insertError(a.getThrowable(), accessId, a);
			}

			// warning访问日志 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (MonitorSwitchConfig.IS_WARNING) {
				// >>>>>>>>>>>>>> 警告信息 begin >>>>>>>>>>>>>>
				BitSet bs = new BitSet();

				// 检查是否有未关闭的数据库连接
				String warn = AccessAnalysisUtils.checkConnection(a.getGetConnTimes(), a.getReleaseConnTimes());
				if (!StringUtils.isEmpty(warn)) {
					BitSet b = new BitSet();
					b.set(1);
					bs.xor(b);
				}

				// 检查for update有没有加上事务
				warn = AccessAnalysisUtils.checkForUpdate(a.getSqlList(), a.getTransactionTimes());
				if (!StringUtils.isEmpty(warn)) {
					BitSet b = new BitSet();
					b.set(2);
					bs.xor(b);
				}

				// 检查非安全SQL，没有加上where条件
				warn = AccessAnalysisUtils.checkUnSafeSql(a.getSqlList());
				if (!StringUtils.isEmpty(warn)) {
					BitSet b = new BitSet();
					b.set(3);
					bs.xor(b);
				}

				// 检查事务个数是否大于1
				warn = AccessAnalysisUtils.checkTransactionTimes(a.getTransactionTimes());
				if (!StringUtils.isEmpty(warn)) {
					BitSet b = new BitSet();
					b.set(4);
					bs.xor(b);
				}

				// 检查多个操作SQL是否没有使用事务
				warn = AccessAnalysisUtils.checkNoTransaction(a.getSqlList(), a.getTransactionTimes());
				if (!StringUtils.isEmpty(warn)) {
					BitSet b = new BitSet();
					b.set(5);
					bs.xor(b);
				}

				// 检查注入SQL
				warn = AccessAnalysisUtils.checkAntiSql(a.getSqlList());
				if (!StringUtils.isEmpty(warn)) {
					BitSet b = new BitSet();
					b.set(6);
					bs.xor(b);
				}

				if (!bs.isEmpty()) {
					impl.insertWarning(a, bs);
				}
			}
		}

		// System.out.println("tttttttttttt:" + (System.currentTimeMillis() - t1));
	}

}
