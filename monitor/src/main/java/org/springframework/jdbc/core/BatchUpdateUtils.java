/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.tomcat.util.threads.TaskThread;
import org.springframework.lang.Nullable;

import com.ppx.cloud.monitor.pojo.AccessLog;

/**
 * Generic utility methods for working with JDBC batch statements.
 * Mainly for internal use within the framework.
 *
 * @author Thomas Risberg
 * @since 3.0
 */
public abstract class BatchUpdateUtils {

	public static int[] executeBatchUpdate(
			String sql, final List<Object[]> batchValues, final int[] columnTypes, JdbcOperations jdbcOperations) {
		
		// dengxz
		AccessLog accessLog = TaskThread.getAccessLog();
		accessLog.addSqlBeginTime(new Date());
        long nanoTime = System.nanoTime();
        
        if (batchValues.size() > 0) {
            // 取第一条的参数
            int sqlIndex = accessLog.getSqlList().size();
            accessLog.addSqlArg(sqlIndex, batchValues.get(0));
        }
        
        
        int[] r = null;
        r = jdbcOperations.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Object[] values = batchValues.get(i);
                        setStatementParameters(values, ps, columnTypes);
                    }
                    @Override
                    public int getBatchSize() {
                        return batchValues.size();
                    }
                });
        
        if (r != null) {
        	accessLog.addSqlCount(r.length);
        }
        accessLog.addSqlSpendTime((System.nanoTime() - nanoTime) / 1000000);
        return r;

		
	}

	protected static void setStatementParameters(Object[] values, PreparedStatement ps, @Nullable int[] columnTypes)
			throws SQLException {

		int colIndex = 0;
		for (Object value : values) {
			colIndex++;
			if (value instanceof SqlParameterValue) {
				SqlParameterValue paramValue = (SqlParameterValue) value;
				StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
			}
			else {
				int colType;
				if (columnTypes == null || columnTypes.length < colIndex) {
					colType = SqlTypeValue.TYPE_UNKNOWN;
				}
				else {
					colType = columnTypes[colIndex - 1];
				}
				StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
			}
		}
	}

}
