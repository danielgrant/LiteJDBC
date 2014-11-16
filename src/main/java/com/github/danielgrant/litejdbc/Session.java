package com.github.danielgrant.litejdbc;

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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface Session {
  
  public Connection getConnection();

  public <T> T findObject(String sql, Object[] parameters, int[] sqlTypes,
      ResultSetProcessor<T> databaseResultSetProcessor);

  public <T> List<T> findList(String sql, Object[] parameters, int[] sqlTypes,
      ResultSetProcessor<T> databaseResultSetProcessor);

  public <T> T findObjectUsingReflection(String sql, Object[] parameters, int[] sqlTypes,
      Class<T> targetClass);

  public <T> List<T> findListUsingReflection(String sql, Object[] parameters, int[] sqlTypes,
      Class<T> targetClass);

  public String findString(String sql, Object[] parameters, int[] sqlTypes);

  public List<String> findStringList(String sql, Object[] parameters, int[] sqlTypes);

  public Integer findInteger(String sql, Object[] parameters, int[] sqlTypes);

  public List<Integer> findIntegerList(String sql, Object[] parameters, int[] sqlTypes);

  public Float findFloat(String sql, Object[] parameters, int[] sqlTypes);

  public List<Float> findFloatList(String sql, Object[] parameters, int[] sqlTypes);

  public Long findLong(String sql, Object[] parameters, int[] sqlTypes);

  public List<Long> findLongList(String sql, Object[] parameters, int[] sqlTypes);

  public Date findDate(String sql, Object[] parameters, int[] sqlTypes);

  public List<Date> findDateList(String sql, Object[] parameters, int[] sqlTypes);

  public Time findTime(String sql, Object[] parameters, int[] sqlTypes);

  public List<Time> findTimeList(String sql, Object[] parameters, int[] sqlTypes);

  public Timestamp findTimestamp(String sql, Object[] parameters, int[] sqlTypes);

  public List<Timestamp> findTimestampList(String sql, Object[] parameters, int[] sqlTypes);

  public int executeUpdate(String sql, Object[] parameters, int[] sqlTypes);
}
