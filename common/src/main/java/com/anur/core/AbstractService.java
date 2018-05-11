package com.anur.core;

import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 基于通用MyBatis Mapper插件的Service接口的实现
 */
public abstract class AbstractService<T> implements Service<T> {

    @Autowired
    protected Mapper<T> mapper;

    private Class<T> modelClass;    // 当前泛型真实类型的Class

    public AbstractService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public int save(T model) {
        return mapper.insertSelective(model);
    }

    @Override
    public int save(List<T> models) {
        return mapper.insertList(models);
    }

    @Override
    public int deleteById(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByIds(String ids) {
        return mapper.deleteByIds(ids);
    }

    @Override
    public int update(T model) {
        return mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public T findById(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public T findExistById(Integer id) {
        T t = mapper.selectByPrimaryKey(id);
        if (t == null) {
            throw new RuntimeException("NOT_EXIST");
        }
        return t;
    }

    @Override
    public T findBy(String fieldName, Object value) throws TooManyResultsException {
        try {
            Constructor<T> constructor = modelClass.getConstructor();
            T model = constructor.newInstance();
            Field field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return mapper.selectOne(model);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<T> findByIds(String ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> findByCondition(Condition condition) {
        return mapper.selectByCondition(condition);
    }

    @Override
    public List<T> findExistByCondition(Condition condition) {
        List<T> tList = mapper.selectByCondition(condition);
        if (tList == null) {
            throw new RuntimeException("NOT_EXIST");
        }
        if (tList.size() == 0) {
            throw new RuntimeException("NOT_EXIST");
        }
        return tList;
    }

    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }
}
