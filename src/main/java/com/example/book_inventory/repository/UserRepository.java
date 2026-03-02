package com.example.book_inventory.repository;

import com.example.book_inventory.model.User.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<UserDocument,String> {

    UserDocument findByUsername(String userName);

    UserDocument findByEmail(String email);

    void deleteById(String userID);

}
