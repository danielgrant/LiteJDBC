package com.github.danielgrant.litejdbc.resultsetprocessors.reflection;

/*
 * Copyright 2014 LiteJDBC
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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.github.danielgrant.litejdbc.ResultSetProcessor;
import com.github.danielgrant.litejdbc.exception.DataAccessSQLException;
import com.github.danielgrant.litejdbc.exception.ExceptionTranslator;
import com.github.danielgrant.litejdbc.util.StringUtils;

public abstract class ReflectionResultSetProcessor<T> implements ResultSetProcessor<T> {

    @Override
    public T processResultSet(ResultSet resultSet) throws DataAccessSQLException {

        Class<T> targetClass = getTargetClass();

        try {
            T targetObject = targetClass.newInstance();

            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                if (isValidField(field)) {
                    PropertyDescriptor propertyDescriptor = createPropertyDescriptor(field, targetClass);
                    Method readMethod = propertyDescriptor.getReadMethod();
                    Method writeMethod = propertyDescriptor.getWriteMethod();

                    if (isValidReadMethod(readMethod) && isValidWriteMethod(writeMethod)) {
                        String columnName = getColumnName(field, writeMethod, readMethod);
                        Object resultSetValue = resultSet.getObject(columnName);

                        writeValueToMethod(targetObject, resultSetValue, writeMethod);
                    }
                }
            }

            return targetObject;
        } catch (InstantiationException e) {
            throw ExceptionTranslator.translateReflectionException("ReflectionResultSetProcessor.processResultSet()", targetClass, e);
        } catch (IllegalAccessException e) {
            throw ExceptionTranslator.translateReflectionException("ReflectionResultSetProcessor.processResultSet()", targetClass, e);
        } catch (IllegalArgumentException e) {
            throw ExceptionTranslator.translateReflectionException("ReflectionResultSetProcessor.processResultSet()", targetClass, e);
        } catch (InvocationTargetException e) {
            throw ExceptionTranslator.translateReflectionException("ReflectionResultSetProcessor.processResultSet()", targetClass, e);
        } catch (IntrospectionException e) {
            throw ExceptionTranslator.translateReflectionException("ReflectionResultSetProcessor.processResultSet()", targetClass, e);
        } catch (SQLException e) {
            throw ExceptionTranslator.translateSQLException("ReflectionResultSetProcessor.processResultSet()", e);
        }
    }

    public abstract Class<T> getTargetClass();

    private PropertyDescriptor createPropertyDescriptor(Field field, Class<T> targetClass) throws IntrospectionException {
        return new PropertyDescriptor(field.getName(), getTargetClass());
    }

    private boolean isValidField(Field field) {
        return field != null && !field.isAnnotationPresent(Transient.class);
    }

    private boolean isValidReadMethod(Method readMethod) {
        return readMethod != null && !readMethod.isAnnotationPresent(Transient.class);
    }

    private boolean isValidWriteMethod(Method writeMethod) {
        return writeMethod != null && !writeMethod.isAnnotationPresent(Transient.class);
    }

    private String getColumnName(Field field, Method writeMethod, Method readMethod) {
        String fieldColumnName = getColumnNameFromAnnotation(field);
        if (fieldColumnName != null) {
            return fieldColumnName;
        } else {
            String writeMethodColumnName = getColumnNameFromAnnotation(writeMethod);
            if (writeMethodColumnName != null) {
                return writeMethodColumnName;
            } else {
                String readMethodColumnName = getColumnNameFromAnnotation(readMethod);
                if (readMethodColumnName != null) {
                    return readMethodColumnName;
                }
            }
        }

        return field.getName();
    }

    private String getColumnNameFromAnnotation(AccessibleObject accessibleObject) {
        Column columnAnnotation = accessibleObject.getAnnotation(Column.class);

        if (columnAnnotation != null && StringUtils.isNotBlank(columnAnnotation.name())) {
            return columnAnnotation.name();
        }

        return null;
    }

    private void writeValueToMethod(Object targetObject, Object resultSetValue, Method writeMethod) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        writeMethod.invoke(targetObject, resultSetValue);
    }
}
