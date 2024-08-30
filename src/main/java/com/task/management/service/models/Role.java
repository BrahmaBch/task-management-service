package com.task.management.service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {
  @Id
  private String id;

  private UserRoles name;

  public Role() {

  }

  public Role(UserRoles name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserRoles getName() {
    return name;
  }

  public void setName(UserRoles name) {
    this.name = name;
  }
}
