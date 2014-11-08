/*! \addtogroup Configuration Configuration 
 * This module is responsible for keeping configuration parameters. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.options;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.mit.csail.sdg.squander.utils.ReflectionUtils;


public class ConfigLoader {
    
    public static boolean checkUnknownOptions(String[] args) {
        boolean ok = true;
        for (String arg : args) {
            if (arg.startsWith("--")) {
                ok = false;
                error("Unknown option: " + arg);
            }
        }
        return ok;
    }

    public static String[] loadOptions(Object confObj, String[] args) {
        Class<?> cls = confObj.getClass();
        List<String> free = new LinkedList<String>();
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                free.add(arg);
                continue;
            }
            String[] pair = arg.substring(2).split("=");
            String optionName = pair[0];
            String optionValue = null;
            if (pair.length == 2)
                optionValue = pair[1];
            String optName = optionName.replaceAll("-", "_");
            Field f = getField(cls, optName);
            if (f == null)
                f = getField(cls, optName.toUpperCase());
            if (f == null) {
                //error("Unknonwn option: " + arg);
                free.add(arg);
                continue;
            }
            
            Class<?> fieldType = f.getType();
            if (fieldType == boolean.class) {
                setBoolean(confObj, optionName, optionValue, f);
            } else if (fieldType == int.class) {
                setInt(confObj, optionName, optionValue, f);
            } else if (fieldType == Class.class) {
                setClass(confObj, optionName, optionValue, f);
            } else if (fieldType.isEnum()) {
                setEnum(confObj, optionName, optionValue, f, fieldType);
            } else if (fieldType == String.class) {
                setString(confObj, optionName, optionValue, f);
            } else {
                error(String.format("Cannot set value for a field of type %s", fieldType.getName()));
            }
        }
        return free.toArray(new String[0]);
    }

    private static Field getField(Class<?> cls, String name) {
        try {
            return ReflectionUtils.getField(cls, name);
        } catch (Exception e) {
            return null;
        }
    }

    private static void setString(Object confObj, String optionName, String optionValue, Field f) {
        ReflectionUtils.setFieldValue(confObj, f, optionValue);
    }

    private static void setEnum(Object confObj, String optionName, String optionValue, Field f,
            Class<?> fieldType) {
        Map<String, Object> enums = new HashMap<String, Object>();
        for (Object enumConst : fieldType.getEnumConstants()) {
            enums.put(enumConst.toString(), enumConst);
        }
        if (optionValue == null)
            error(String.format("Must provide a value for enum option %s. Legal values: %s", 
                    optionName, enums.keySet()));
        String val = optionValue.replaceAll("-", "_");
        Object obj = enums.get(val);
        if (obj == null) {
            error(String.format("Invalid value for option %s: %s. Legal values: %s", 
                    optionName, optionValue, enums.keySet()));
        } else {
            ReflectionUtils.setFieldValue(confObj, f, obj);
        }
    }

    private static void setClass(Object confObj, String optionName, String optionValue, Field f) {
        try {
            ReflectionUtils.setFieldValue(confObj, f, Class.forName(optionValue));
        } catch (ClassNotFoundException e) {
            error("Could not find a class with name: " + optionValue);
        }
    }

    private static void setInt(Object confObj, String optionName, String optionValue, Field f) {
        if (optionValue == null)
            error(String.format("Must provide a value for integer option %s", optionName));
        try {
            ReflectionUtils.setFieldValue(confObj, f, Integer.parseInt(optionValue));
        } catch (NumberFormatException e) {
            error(String.format("Invalid value for integer option %s: %s", optionName, optionValue));
        }
    }

    private static void setBoolean(Object confObj, String optionName, String optionValue, Field f) {
        if (optionValue != null) {
            if (optionValue.toLowerCase().equals("true"))
                ReflectionUtils.setFieldValue(confObj, f, true);
            else if (optionValue.toLowerCase().equals("false"))
                ReflectionUtils.setFieldValue(confObj, f, false);
            else
                error(String.format("Invalid value for boolean option %s: %s", optionName, optionValue));
        } else {
            ReflectionUtils.setFieldValue(confObj, f, true);
        }
    }

    private static void error(String msg) {
        System.err.println(msg);
    }

}
/*! @} */
