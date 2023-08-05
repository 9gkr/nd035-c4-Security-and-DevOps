package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {

    /**
     * This method helps us to inject objects to other objects. Code is from Udacity Java Web Developer Nanodegree.
     * @param target the object you intend to inject to
     * @param fieldName the name of the field that belongs to target
     * @param toInject the object you intend to inject
     */
    public static void injectObjects(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            // .isAccessible() is depreciated. use canAccess() instead (which requires to upgrade java version to 9+)
            // .canAccess() checks if field is public (return true) or private (return false) in target
            if (!field.canAccess(target)){
                field.setAccessible(true);
                wasPrivate = true;
            }
            field.set(target, toInject);

            if (wasPrivate){
                field.setAccessible(false);
            }
        }
        catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
