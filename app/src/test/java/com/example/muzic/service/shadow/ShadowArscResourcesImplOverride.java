//package com.example.muzic.service.shadow;
//
//import static org.mockito.Mockito.mock;
//import static org.robolectric.shadow.api.Shadow.directlyOn;
//import static org.robolectric.util.ReflectionHelpers.ClassParameter.from;
//
//import android.content.res.ColorStateList;
//import android.content.res.ComplexColor;
//import android.content.res.Resources;
//import android.content.res.ResourcesImpl;
//import android.graphics.drawable.Drawable;
//import android.util.TypedValue;
//
//import org.robolectric.annotation.Implementation;
//import org.robolectric.annotation.Implements;
//import org.robolectric.annotation.RealObject;
//
//
//@Implements(value = ResourcesImpl.class)
//public class ShadowArscResourcesImplOverride {
//    @RealObject
//    ResourcesImpl realResourcesImpl;
//
//    @Implementation
//    public Drawable loadDrawableForCookie(Resources wrapper, TypedValue value,
//                                          int id, int density) /*throws Exception*/ {
//
//        Drawable drawable = mock(Drawable.class);
//        try {
//            drawable = directlyOn(realResourcesImpl, ResourcesImpl.class, "loadDrawableForCookie",
//                    from(Resources.class, wrapper),
//                    from(TypedValue.class, value),
//                    from(int.class, id),
//                    from(int.class, density));
//        } catch (Exception e) {
//        }
//
//        return drawable;
//    }
//
//    @Implementation
//    public ComplexColor loadComplexColorForCookie(Resources wrapper, TypedValue value, int id,
//                                                  Resources.Theme theme) {
//        ComplexColor complexColor = mock(ComplexColor.class);
//        try {
//            complexColor = directlyOn(realResourcesImpl, ResourcesImpl.class, "loadComplexColorForCookie",
//                    from(Resources.class, wrapper),
//                    from(TypedValue.class, value),
//                    from(int.class, id),
//                    from(Resources.Theme.class, theme));
//        } catch (Exception e) {
//        }
//        return complexColor;
//    }
//
//    @Implementation
//    public ColorStateList loadColorStateList(Resources wrapper, TypedValue value, int id,
//                                             Resources.Theme theme) {
//        ComplexColor complexColor = (ComplexColor) mock(ColorStateList.class);
//        try {
//            complexColor = directlyOn(realResourcesImpl, ResourcesImpl.class, "loadColorStateList",
//                    from(Resources.class, wrapper),
//                    from(TypedValue.class, value),
//                    from(int.class, id),
//                    from(Resources.Theme.class, theme));
//
//        } catch (Exception e) {
//        }
//        return (ColorStateList) complexColor;
//    }
//}