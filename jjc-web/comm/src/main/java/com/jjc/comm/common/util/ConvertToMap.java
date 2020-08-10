package com.jjc.comm.common.util;

import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 对象转换成map
 * Created by Michael.Jing on 2017/5/17.
 */
public class ConvertToMap {

    public static Map<String, Object> ObjToMap(Object obj){
        Map<String, Object> reMap = new HashMap<String, Object>();
         if (obj == null){
             return null;
         }
        Field[] fields1 = obj.getClass().getDeclaredFields();
        Field[] fields2 = obj.getClass().getSuperclass() != null?obj.getClass().getSuperclass().getDeclaredFields():null;
        Field[] fields3 = obj.getClass().getSuperclass() != null?(obj.getClass().getSuperclass().getSuperclass()!=null?obj.getClass().getSuperclass().getSuperclass().getDeclaredFields():null):null;

        try {
                try {
                   if(fields3 != null){
                       for(int i=0;i<fields3.length;i++){
                           Field f = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(fields3[i].getName());
                           f.setAccessible(true);
                           Object o = f.get(obj);
                           reMap.put(fields3[i].getName(), o);
                       }
                   }

                    if(fields2 != null){
                        for(int i=0;i<fields2.length;i++){
                            Field f = obj.getClass().getSuperclass().getDeclaredField(fields2[i].getName());
                            f.setAccessible(true);
                            Object o = f.get(obj);
                            reMap.put(fields2[i].getName(), o);
                        }
                    }
                    if(fields1 != null){
                        for(int i=0;i<fields1.length;i++){
                            Field f = obj.getClass().getDeclaredField(fields1[i].getName());
                            f.setAccessible(true);
                            Object o = f.get(obj);
                            reMap.put(fields1[i].getName(), o);
                        }
                    }

                } catch (NoSuchFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reMap;
    }

    /**
     * 转化为按顺序的
     * @param obj
     * @return
     */
    public static Map<String, Object> ObjToLinkMap(Object obj){
        Map<String, Object> reMap = new LinkedHashMap<String, Object>();
        if (obj == null){
            return null;
        }
        Field[] fields1 = obj.getClass().getDeclaredFields();
        Field[] fields2 = obj.getClass().getSuperclass() != null?obj.getClass().getSuperclass().getDeclaredFields():null;
        Field[] fields3 = obj.getClass().getSuperclass() != null?(obj.getClass().getSuperclass().getSuperclass()!=null?obj.getClass().getSuperclass().getSuperclass().getDeclaredFields():null):null;

        try {
            try {
                if(fields3 != null){
                    for(int i=0;i<fields3.length;i++){
                        Field f = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(fields3[i].getName());
                        f.setAccessible(true);
                        Object o = f.get(obj);
                        reMap.put(fields3[i].getName(), o);
                    }
                }

                if(fields2 != null){
                    for(int i=0;i<fields2.length;i++){
                        Field f = obj.getClass().getSuperclass().getDeclaredField(fields2[i].getName());
                        f.setAccessible(true);
                        Object o = f.get(obj);
                        reMap.put(fields2[i].getName(), o);
                    }
                }
                if(fields1 != null){
                    for(int i=0;i<fields1.length;i++){
                        Field f = obj.getClass().getDeclaredField(fields1[i].getName());
                        f.setAccessible(true);
                        Object o = f.get(obj);
                        reMap.put(fields1[i].getName(), o);
                    }
                }

            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reMap;
    }



    /**
     * 将 Map对象转化为JavaBean
     * @param map
     * @param T
     * @return
     * @throws Exception
     */
    public static <T> T convertMap2Bean(Map<String, Object> map, Class<T> T)
            throws Exception {
        if (map ==null  || map.size() == 0)
        {
            return null;
        }
        //获取map中所有的key值，全部更新成大写，添加到keys集合中,与mybatis中驼峰命名匹配
        Object mvalue =null ;
        Map<String, Object> newMap = new HashMap<>();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while(it.hasNext()){
            String key = it.next().getKey();
            mvalue = map.get(key);
          /*  if (key.indexOf(CharacterConstant.UNDERLINE) != -1)
            {
                key = key.replaceAll(CharacterConstant.UNDERLINE, "");
            }*/
            newMap.put(key.toUpperCase(Locale.US), mvalue);
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(T);
        T bean = T.newInstance();
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0, n = propertyDescriptors.length; i < n; i++)
        {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            String upperPropertyName = propertyName.toUpperCase();

            if (newMap.keySet().contains(upperPropertyName))
            {
                Object value = newMap.get(upperPropertyName);
                //这个方法不会报参数类型不匹配的错误。
                BeanUtils.copyProperty(bean, propertyName, value);
            }
        }
        return bean;
    }


}
