package com.bootcamp.services;

import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.exceptions.DatabaseException;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.commons.ws.utils.RequestParser;
import com.bootcamp.crud.UserCRUD;
import com.bootcamp.entities.User;
import com.bootcamp.security.JwtAuthentification;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Bignon on 11/27/17.
 */

@Component
public class UserService implements DatabaseConstants{

    UserCRUD userCRUD;

    @PostConstruct
    public void init(){
        userCRUD = new UserCRUD();
    }

    public void create(User user) throws SQLException {
         userCRUD.create(user);
    }

    public void update(User user) throws SQLException {
        userCRUD.update(user);
    }

    public User delete(int id) throws SQLException {
        User user = read(id);
        userCRUD.delete(user);

        return user;
    }

    public User read(int id) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("id", "=", id));
        List<User> users = userCRUD.read(criterias);

        return users.get(0);
    }
    
    public boolean getByLoginAndPwd(int id, String pwd) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("id", "=", id));
        criterias.addCriteria(new Criteria("pwd", "=", pwd));
        List<User> users = userCRUD.read(criterias);
        
        if(users.isEmpty())
            return false;
        else
            return true;

        //return users.get(0);
    }



    public List<User> read(HttpServletRequest request) throws SQLException, IllegalAccessException, DatabaseException, InvocationTargetException {
        Criterias criterias = RequestParser.getCriterias(request);
        List<String> fields = RequestParser.getFields(request);
        List<User> users = null;
        if(criterias == null && fields == null)
           users =  userCRUD.read();
        else if(criterias!= null && fields==null)
            users = userCRUD.read(criterias);
        else if(criterias== null && fields!=null)
            users = userCRUD.read(fields);
        else
            users = userCRUD.read(criterias, fields);

        return users;
    }
//    JwtAuthentification authentification = new JwtAuthentification();
//    public String generateTokenFromAuthentificated(User user){
//        try {
//          JwtAuthentification.addAuthentication(user);
//        } catch (Exception e) {
//            
//        }
//    }

}
