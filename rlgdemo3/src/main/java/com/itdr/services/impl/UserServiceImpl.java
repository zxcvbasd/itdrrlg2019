package com.itdr.services.impl;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.common.TokenCache;
import com.itdr.mappers.UsersMapper;
import com.itdr.pojo.Users;
import com.itdr.services.UserService;
import com.itdr.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UsersMapper usersMapper;

    //用户登录
    @Override
    public ServerResponse<Users> login(String username, String password) {
        if (username == null || username.equals("")) {
            return ServerResponse.defeatedRS("用户名不能为空");
        }
        if (password == null || password.equals("")) {
            return ServerResponse.defeatedRS("密码不能为空");
        }

        //根据用户名查找该用户名是否存在
        int i = usersMapper.selectByUserNameOrEmail(username, "username");
        if (i <= 0) {
            return ServerResponse.defeatedRS("用户不存在");
        }

        //MD5加密
        String md5Password = MD5Utils.getMD5Code(password);

        //根据用户名和密码查询用户名是否存在
        Users u = usersMapper.selectByUsernameAndPassword(username, md5Password);
        if (u == null) {
            return ServerResponse.defeatedRS("账号或密码错误");
        }

        //封装数据并返回
        ServerResponse sr = ServerResponse.successRS(u);
        return sr;
    }

    //用户注册
    @Override
    public ServerResponse<Users> register(Users u) {
        if (u.getUsername() == null || u.getUsername().equals("")) {
            return ServerResponse.defeatedRS("账户名不能为空");
        }
        if (u.getPassword() == null || u.getPassword().equals("")) {
            return ServerResponse.defeatedRS("密码不能为空");
        }

        //检查注册用户名是否存在
        int i2 = usersMapper.selectByUserNameOrEmail(u.getUsername(), "username");
        if (i2 > 0) {
            return ServerResponse.defeatedRS("要注册的用户已经存在");
        }

        //md5加密
        u.setPassword(MD5Utils.getMD5Code(u.getPassword()));

        int i = usersMapper.insert(u);
        if (i <= 0) {
            return ServerResponse.defeatedRS("用户注册失败");
        }
        return ServerResponse.successRS(200, "用户注册成功");
    }

    //检查用户名或邮箱是否存在
    @Override
    public ServerResponse<Users> checkUserName(String str, String type) {
        if (str == null || str.equals("")) {
            return ServerResponse.defeatedRS("参数不能为空");
        }
        if (type == null || type.equals("")) {
            return ServerResponse.defeatedRS("参数类型不能为空");
        }
        int i = usersMapper.selectByUserNameOrEmail(str, type);
        if (i > 0 && type.equals("username")) {
            return ServerResponse.defeatedRS("用户名已经存在");
        }
        if (i > 0 && type.equals("email")) {
            return ServerResponse.defeatedRS("邮箱已经存在");
        }

        return ServerResponse.successRS(200, "校验成功");
    }

    //获取当前登录用户的详细信息
    @Override
    public ServerResponse getInforamtion(Users users) {
        Users users1 = usersMapper.selectByPrimaryKey(users.getId());
        if (users == null) {
            return ServerResponse.defeatedRS("用户不存在");
        }
        users1.setPassword("");
        return ServerResponse.successRS(users1);
    }

    //登录状态更新个人信息
    @Override
    public ServerResponse update_information(Users u) {
        int i2 = usersMapper.selectByEmailAndId(u.getEmail(), u.getId());
        if (i2 > 0) {
            return ServerResponse.defeatedRS("要更新的邮箱已经存在");
        }

        int i = usersMapper.updateByPrimaryKeySelective(u);
        if (i <= 0) {
            return ServerResponse.defeatedRS("更新失败");
        }
        return ServerResponse.successRS("更新个人信息成功");
    }

    //忘记密码
    @Override
    public ServerResponse<Users> forgetGetQuestion(String username) {
        if (username == null || username.equals("")) {
            return ServerResponse.defeatedRS("参数不能为空");
        }

        int i = usersMapper.selectByUserNameOrEmail(username, Const.USERNAME);
        if (i <= 0) {
            return ServerResponse.defeatedRS("用户名不存在");
        }

        String question = usersMapper.selectByUserName(username);
        if (question == null || "".equals(question)) {
            return ServerResponse.defeatedRS("该用户未设置找回密码问题");
        }
        return ServerResponse.successRS(question);
    }

    //提交问题答案
    @Override
    public ServerResponse<Users> forgetCheckAnswer(String username, String question, String answer) {
        //参数是否为空
        if (username == null || username.equals("")) {
            return ServerResponse.defeatedRS("用户名不能为空");
        }
        if (question == null || question.equals("")) {
            return ServerResponse.defeatedRS("问题不能为空");
        }
        if (answer == null || answer.equals("")) {
            return ServerResponse.defeatedRS("答案不能为空");
        }

        //用户是否存在

        int i = usersMapper.selectByUsernameAndQuestionAndAnswer(username, question, answer);
        if (i <= 0) {
            return ServerResponse.defeatedRS("问题答案不匹配");
        }

        //产生随机字符令牌
        String token = UUID.randomUUID().toString();
        //把令牌放入缓存中，这里使用的是Google的guava缓存，后期会使用redis代替
        TokenCache.set("token_" + username, token);

        return ServerResponse.successRS(token);
    }


    //忘记密码的重设密码
    @Override
    public ServerResponse<Users> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //参数是否为空
        if (username == null || username.equals("")) {
            return ServerResponse.defeatedRS("用户名不能为空");
        }
        if (passwordNew == null || passwordNew.equals("")) {
            return ServerResponse.defeatedRS("新密码不能为空");
        }
        if (forgetToken == null || forgetToken.equals("")) {
            return ServerResponse.defeatedRS("非法的令牌参数");
        }

        //判断缓存中的token
        String token = TokenCache.get("token_" + username);
        if (token == null || token.equals("")) {
            return ServerResponse.defeatedRS("token过期了");
        }
        if (!token.equals(forgetToken)) {
            return ServerResponse.defeatedRS("非法的token");
        }

        //MD5加密
        String md5passwordNew = MD5Utils.getMD5Code(passwordNew);

        int i = usersMapper.updateByUserNameAndPassword(username, md5passwordNew);
        if (i <= 0) {
            return ServerResponse.defeatedRS("修改密码失败");
        }
        return ServerResponse.successRS("修改密码成功");
    }

    //登录状态重置密码
    @Override
    public ServerResponse<Users> resetPassword(Users users, String passwordOld, String passwordNew) {
        //参数是否为空
        if (passwordOld == null || passwordOld.equals("")) {
            return ServerResponse.defeatedRS("参数不能为空");
        }
        if (passwordNew == null || passwordNew.equals("")) {
            return ServerResponse.defeatedRS("参数不能为空");
        }

        //MD5加密
        String md5passwordOld = MD5Utils.getMD5Code(passwordOld);

        int i = usersMapper.selectByIdAndPassword(users.getId(), md5passwordOld);
        if (i <= 0) {
            return ServerResponse.defeatedRS("旧密码输入错误");
        }

        //MD5加密
        String md5passwordNew = MD5Utils.getMD5Code(passwordNew);

        int i2 = usersMapper.updateByUserNameAndPassword(users.getUsername(),md5passwordNew);
        if (i2<=0){
            return ServerResponse.defeatedRS("修改密码失败");
        }
        return ServerResponse.successRS("修改密码成功");
    }
}
