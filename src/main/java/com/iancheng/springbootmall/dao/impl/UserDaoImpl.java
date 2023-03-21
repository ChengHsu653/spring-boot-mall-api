package com.iancheng.springbootmall.dao.impl;

import com.iancheng.springbootmall.constant.Role;
import com.iancheng.springbootmall.dao.UserDao;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User getUserById(Integer userId) {
        String sql = "SELECT user_id, email, password, created_date, last_modified_date, role " +
                     "FROM `user` " +
                     "WHERE user_id = :userId";

        var map = new HashMap<String, Object>();
        map.put("userId", userId);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (userList.size() > 0) return userList.get(0);
        else return null;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, email, password, created_date, last_modified_date, role " +
                     "FROM `user` " +
                     "WHERE email = :email";

        var map = new HashMap<String, Object>();
        map.put("email", email);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (userList.size() > 0) return userList.get(0);
        else return null;
    }

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        String sql = "INSERT INTO `user`(email, password, created_date, last_modified_date, role) " +
                     "VALUES (:email, :password, :createdDate, :lastModifiedDate, :role)";

        var map = new HashMap<String, Object>();
        map.put("email", userRegisterRequest.getEmail());
        map.put("password", userRegisterRequest.getPassword());

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);
        
        map.put("role", Role.UNVERIFIED.toString());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int userId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        return userId;
    }

	@Override
	public void activateUser(User user) {
		String sql = "UPDATE `user` " + 
				     "SET role = :role, last_modified_date = :lastModifiedDate " + 
				     "WHERE email = :email";
		
		var map = new HashMap<String, Object>();
        map.put("role", Role.MEMBER.toString());
        map.put("email", user.getEmail());
        
        Date now = new Date();
        map.put("lastModifiedDate", now);

        namedParameterJdbcTemplate.update(sql, map);
	}


}
