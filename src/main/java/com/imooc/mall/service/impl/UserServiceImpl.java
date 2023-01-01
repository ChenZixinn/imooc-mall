package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public void register(String userName, String password) throws ImoocMallException {
        // 检查是否重名
        User result = userMapper.selectByName(userName);
        if (result != null) {
            // 有重名，返回错误
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }

        // 存入数据库
        User user = new User();
        user.setUsername(userName);
        try {
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // insertSelective只存入存在的字段
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            // 插入失败，抛出异常
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws ImoocMallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    /**
     * 更新个性签名
     *
     * @param user 存有主键和更新信息的user对象
     */
    @Override
    public void updateSignature(User user) throws ImoocMallException {
        int count = userMapper.updateByPrimaryKeySelective(user);
        if (count > 1) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.SYSTEM_EXCEPTION);
        }
    }

    @Override
    public boolean checkAdmin(User user) throws ImoocMallException {

        return user.getRole().equals(2);
    }
}
