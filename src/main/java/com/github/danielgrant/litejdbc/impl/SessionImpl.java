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

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.github.danielgrant.litejdbc.ResultSetProcessor;
import com.github.danielgrant.litejdbc.Session;
import com.github.danielgrant.litejdbc.exception.ExceptionTranslator;
import com.github.danielgrant.litejdbc.resultsetprocessors.reflection.ReflectionResultSetProcessor;
import com.github.danielgrant.litejdbc.resultsetprocessors.simple.SimpleResultSetProcessor;
import com.github.danielgrant.litejdbc.util.JdbcUtils;

public class SessionImpl implements Session {

  private String user;
  private String password;
  private String url;

  public SessionImpl(String user, String password, String url) {
    this.user = user;
    this.password = password;
    this.url = url;
  }

  @Override
  public Connection getConnection() {
    try {
      return DriverManager.getConnection(url, user, password);
    } catch (SQLException sqlException) {
      throw ExceptionTranslator.translateSQLException("Session.getConnection()", sqlException);
    }
  }

  @Override
  public <T> T findObject(String sql, Object[] parameters, int[] sqlTypes,
      ResultSetProcessor<T> resultSetProcessor) {
    List<T> results = findList(sql, parameters, sqlTypes, resultSetProcessor);

    if (results == null || results.isEmpty()) {
      return null;
    } else {
      return results.get(0);
    }
  }

  @Override
  public <T> List<T> findList(String sql, Object[] parameters, int[] sqlTypes,
      ResultSetProcessor<T> resultSetProcessor) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      connection = getConnection();
      preparedStatement = prepareStatement(connection, sql, parameters, sqlTypes);

      resultSet = preparedStatement.executeQuery();

      List<T> results = new ArrayList<T>();
      if (resultSet != null) {
        while (resultSet.next()) {
          results.add(resultSetProcessor.processResultSet(resultSet));
        }
      }
      return results;
    } catch (SQLException sqlException) {
      throw ExceptionTranslator.translateSQLException("Session.findList()", sqlException);
    } finally {
      JdbcUtils.closeResultSet(resultSet);
      JdbcUtils.closePreparedStatement(preparedStatement);
      JdbcUtils.closeConnection(connection);
    }
  }

  @Override
  public <T> T findObjectUsingReflection(String sql, Object[] parameters, int[] sqlTypes,
      final Class<T> targetClass) {
    return findObject(sql, parameters, sqlTypes, new ReflectionResultSetProcessor<T>() {
      @Override
      public Class<T> getTargetClass() {
        return targetClass;
      }
    });
  }

  @Override
  public <T> List<T> findListUsingReflection(String sql, Object[] parameters, int[] sqlTypes,
      final Class<T> targetClass) {
    return findList(sql, parameters, sqlTypes, new ReflectionResultSetProcessor<T>() {
      @Override
      public Class<T> getTargetClass() {
        return targetClass;
      }
    });
  }

  @Override
  public String findString(String sql, Object[] parameters, int[] sqlTypes) {
    return findObject(sql, parameters, sqlTypes, new SimpleResultSetProcessor<String>());
  }

  @Override
  public List<String> findStringList(String sql, Object[] parameters, int[] sqlTypes) {
    return findList(sql, parameters, sqlTypes, new SimpleResultSetProcessor<String>());
  }

  @Override
  public Integer findInteger(String sql, Object[] parameters, int[] sqlTypes) {
    return findObject(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Integer>());
  }

  @Override
  public List<Integer> findIntegerList(String sql, Object[] parameters, int[] sqlTypes) {
    return findList(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Integer>());
  }

  @Override
  public Float findFloat(String sql, Object[] parameters, int[] sqlTypes) {
    return findObject(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Float>());
  }

  @Override
  public List<Float> findFloatList(String sql, Object[] parameters, int[] sqlTypes) {
    return findList(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Float>());
  }

  @Override
  public Long findLong(String sql, Object[] parameters, int[] sqlTypes) {
    return findObject(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Long>());
  }

  @Override
  public List<Long> findLongList(String sql, Object[] parameters, int[] sqlTypes) {
    return findList(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Long>());
  }

  @Override
  public Date findDate(String sql, Object[] parameters, int[] sqlTypes) {
    return findObject(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Date>());
  }

  @Override
  public List<Date> findDateList(String sql, Object[] parameters, int[] sqlTypes) {
    return findList(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Date>());
  }

  @Override
  public Time findTime(String sql, Object[] parameters, int[] sqlTypes) {
    return findObject(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Time>());
  }

  @Override
  public List<Time> findTimeList(String sql, Object[] parameters, int[] sqlTypes) {
    return findList(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Time>());
  }

  @Override
  public Timestamp findTimestamp(String sql, Object[] parameters, int[] sqlTypes) {
    return findObject(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Timestamp>());
  }

  @Override
  public List<Timestamp> findTimestampList(String sql, Object[] parameters, int[] sqlTypes) {
    return findList(sql, parameters, sqlTypes, new SimpleResultSetProcessor<Timestamp>());
  }

  @Override
  public int executeUpdate(String sql, Object[] parameters, int[] sqlTypes) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
      connection = getConnection();
      preparedStatement = prepareStatement(connection, sql, parameters, sqlTypes);

      return preparedStatement.executeUpdate();
    } catch (SQLException sqlException) {
      throw ExceptionTranslator.translateSQLException("Session.executeUpdate()", sqlException);
    } finally {
      JdbcUtils.closePreparedStatement(preparedStatement);
      JdbcUtils.closeConnection(connection);
    }
  }

  private PreparedStatement prepareStatement(Connection connection, String sql,
      Object[] parameters, int[] sqlTypes) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(sql);

    if (parameters != null && sqlTypes != null) {
      for (int i = 1; i <= parameters.length; i++) {
        int arrayLoc = i - 1;

        Object parameter = parameters[arrayLoc];
        int sqlType = sqlTypes[arrayLoc];

        preparedStatement.setObject(i, parameter, sqlType);
      }
    }

    return preparedStatement;
  }
}
