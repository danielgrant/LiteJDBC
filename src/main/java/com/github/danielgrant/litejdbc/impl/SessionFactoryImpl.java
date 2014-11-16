package com.github.danielgrant.litejdbc.impl;

/*
 * Copyright 2014 Daniel Grant
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.sql.SQLException;

import com.github.danielgrant.litejdbc.Session;
import com.github.danielgrant.litejdbc.SessionFactory;

public class SessionFactoryImpl implements SessionFactory {
  
  @Override
  public Session getDatabaseSession(String user, String password, String url, String driver)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    Class.forName(driver).newInstance();
    return new SessionImpl(user, password, url);
  }
}
