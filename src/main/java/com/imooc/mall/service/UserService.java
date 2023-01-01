package com.imooc.mall.service;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.User;

public interface UserService {
    User getUser(Integer id);

    void register(String userName, String password) throws ImoocMallException;

    User login(String userName, String password) throws ImoocMallException;

    void updateSignature(User user) throws ImoocMallException;

    boolean checkAdmin(User user) throws ImoocMallException;
}
