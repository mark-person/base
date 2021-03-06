package com.ppx.cloud.config.set;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.config.pojo.Config;
import com.ppx.cloud.config.pojo.ConfigExecResult;

@Controller
public class ConfigController {

	@Autowired
	private ConfigServImpl impl;
	
	public ModelAndView config(ModelAndView mv) {
		mv.addObject("list", list(new Page(), new Config()));
		return mv;
	}
	
	public Map<?, ?> list(Page page, Config pojo) {
		return ReturnMap.of(page, impl.list(page, pojo));
	}
	
	public Map<?, ?> update(@RequestParam String configName, @RequestParam String configValue) {
        return impl.update(configName, configValue);
    }
	
	
	// configExecResult
	public ModelAndView configExecResult(ModelAndView mv) {
		mv.addObject("list", listConfigExecResult(new Page(), new ConfigExecResult()));
		return mv;
	}
	
	public Map<?, ?> listConfigExecResult(Page page, ConfigExecResult pojo) {
		return ReturnMap.of(page, impl.listConfigExecResult(page, pojo));
	}
	
}
