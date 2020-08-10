package com.jjc.comm.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author huoquan
 * @date 2018/12/4.
 */
public class CompareUtil {
    private static Logger logger = LoggerFactory.getLogger(CompareUtil.class);

    /**
     * 比较两个实体属性值，返回一个map以有差异的属性名为key，value为一个list分别存obj1,obj2此属性名的值
     * @param obj1 进行属性比较的对象1
     * @param obj2 进行属性比较的对象2
     * @param compareArr 选择要比较的属性数组
     * @return 属性差异比较结果map
     */
    public static Map<String, List<Object>> compareFields(Object obj1, Object obj2, String[] compareArr) {
        try {
            Map<String, List<Object>> map = new HashMap<String, List<Object>>();
            List<String> ignoreList = null;
            if (compareArr != null && compareArr.length > 0) {
                // array转化为list
                ignoreList = Arrays.asList(compareArr);
            }
            // 只有两个对象都是同一类型的才有可比性
            if (obj1.getClass() == obj2.getClass()) {
                Class clazz = obj1.getClass();
                // 获取object的属性描述
                PropertyDescriptor[] pds = Introspector.getBeanInfo(clazz,
                        Object.class).getPropertyDescriptors();
                // 这里就是所有的属性了
                for (PropertyDescriptor pd : pds) {
                    // 属性名
                    String name = pd.getName();
                    // 如果当前属性选择进行比较，跳到下一次循环
                    if (ignoreList != null && ignoreList.contains(name)) {
                        // get方法
                        Method readMethod = pd.getReadMethod();
                        // 在obj1上调用get方法等同于获得obj1的属性值
                        Object objBefore = readMethod.invoke(obj1);
                        // 在obj2上调用get方法等同于获得obj2的属性值
                        Object objAfter = readMethod.invoke(obj2);
                        if (objBefore instanceof Timestamp) {
                            objBefore = new Date(((Timestamp) objBefore).getTime());
                        }
                        if (objAfter instanceof Timestamp) {
                            objAfter = new Date(((Timestamp) objAfter).getTime());
                        }
                        if (objBefore == null && objAfter == null) {
                            continue;
                        } else if (objBefore == null && objAfter != null) {
                            List<Object> list = new ArrayList<Object>();
                            list.add(objBefore);
                            list.add(objAfter);
                            map.put(name, list);
                            continue;
                        }
                        // 比较这两个值是否相等,不等则放入map
                        if (!objBefore.equals(objAfter)) {
                            List<Object> list = new ArrayList<Object>();
                            list.add(objBefore);
                            list.add(objAfter);
                            map.put(name, list);
                        }
                    }
                }
            }
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }


    /**
     * 判断是否相同
     * @param obj1
     * @param obj2
     * @param compareArr 需要比较的属性
     * @return true即比较的两个对象相同，false比较的对象不同
     */
    public static Boolean compareSame(Object obj1, Object obj2, String[] compareArr) {
        Map<String, List<Object>> map=compareFields(obj1,obj2,compareArr);
        if(map!=null&&map.size()==0){
            return true;
        }
        return false;
    }
}
