package com.jjc.api.controller.system;

import com.github.pagehelper.PageInfo;
import com.jjc.api.commons.aop.ApiTokenAuthorize;
import com.jjc.api.model.system.SysUser;
import com.jjc.api.services.system.ISysUserService;
import com.jjc.comm.common.sys.ApiResult;
import com.jjc.comm.common.sys.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Demo class
 *
 * @author Allion.li
 * @Date 2020/7/10
 */
@Api(description = "用户操作接口")
@RestController
@RequestMapping("/sysUser")
public class SysUserController extends BaseController{

    public static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private ISysUserService iSysUserService;


    @ApiOperation(value = "getAllLists.do", notes="获取全部用户列表")
    @RequestMapping(value = "/getAllLists.do", method = RequestMethod.POST)
    @ResponseBody
    @ApiTokenAuthorize
    public ApiResult getAllLists(HttpServletRequest request){
        ApiResult result = new ApiResult();
        List<SysUser> sysUserList=iSysUserService.listAll();
        result.setObj(sysUserList);
        return result;
    }


}
