package com.jjc.api.controller.system;

import com.jjc.api.commons.aop.ApiTokenAuthorize;
import com.jjc.api.commons.properties.CommonConfig;
import com.jjc.api.model.system.SysUser;
import com.jjc.api.model.system.ext.SysUserModify;
import com.jjc.api.services.system.ISysUserService;
import com.jjc.comm.common.exception.InvalidParamException;
import com.jjc.comm.common.sys.Result;
import com.jjc.comm.common.util.ToolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author Allion.li
 * @Date 2020/7/29
 */
@Api(description = "用户登录接口")
@RestController
public class LoginController extends BaseController{

    public static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private CommonConfig commonConfig;


    /**
     * 登录
     * @param request
     * @param username 账号
     * @param password 密码
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "/login.do", notes="登录接口")
    @ApiTokenAuthorize
    public Result clearSub(HttpServletRequest request, @ApiParam(value = "用户名") @RequestParam(defaultValue = "")String username, @ApiParam(value = "密码") @RequestParam(defaultValue = "")String password){
        Result result = new Result();
        //验证账号和密码
        SysUserModify sysUserModify=validUser(username,password);
        result.setBizData(sysUserModify);
        return result;
    }

    /**
     * 验证账号密码
     * @return 返回用户信息
     */
    private SysUserModify validUser(String username, String password){
        SysUserModify sysUserModify=new SysUserModify();
        //验证角色和权限
        //使用租户id，通过数据库进行验证
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete","0");
        List<SysUser> adminList=iSysUserService.selectByExample(example);

        //当前表没有此账号,使用配置都默认账号
        if(username.equals(commonConfig.getSuperUserName())
                &&password.equals(commonConfig.getSuperPwd())){
            sysUserModify.setId(0);
            sysUserModify.setLoginId(username);
            sysUserModify.setUserName(username);
        } else{
            //当前用户表不为空，则用表的账号密码匹配
            criteria.andEqualTo("loginId",username);
            adminList=iSysUserService.selectByExample(example);
            if(adminList!=null&&adminList.size()>0){
                SysUser sysUser=adminList.get(0);
                if("1".equals(sysUser.getIsDelete())){
                    //账号被冻结
                    throw new InvalidParamException("当前账号不存在");
                }
                if("0".equals(sysUser.getStatus())){
                    //账号被冻结
                    throw new InvalidParamException("当前账号被禁用");
                }
                criteria.andEqualTo("userPwd",ToolUtil.encryptPassword(username,password));
                adminList=iSysUserService.selectByExample(example);
                if(adminList!=null&&adminList.size()>0){
                    //账号密码都正确，登陆成功
                    SysUser extSysuser=iSysUserService.selectByLoginId(username);
                    sysUserModify.setId(extSysuser.getId());
                    sysUserModify.setLoginId(extSysuser.getLoginId());
                    sysUserModify.setUserName(extSysuser.getUserName());
                    sysUserModify.setObjectId(extSysuser.getObjectId());
                   // sysUserModify.setSysUserRoleList(extSysuser.getSysUserRoleList());
                }else{  //密码错误
                    throw new InvalidParamException("当前账号密码错误");
                }
            }else{
                throw new InvalidParamException("当前账号不存在");  //用户不存在
            }
        }
        if(sysUserModify==null|| ToolUtil.isEmpty(sysUserModify.getId())){
            throw new InvalidParamException("当前账号不存在");  //用户不存在
        }
        return sysUserModify;
    }

}
