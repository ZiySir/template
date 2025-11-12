package me.ziyframework.web.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import me.ziyframework.core.tuple.Tuple2;
import me.ziyframework.core.tuple.Tuples;
import me.ziyframework.web.common.exception.GlobalException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * 分页工具类.
 * created in 2025-07
 *
 * @author ziy
 */
public final class PageUtil {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final IPage<?> EMPTY_MP_PAGE = Page.of(0, 0);

    private PageUtil() {}

    /**
     * 获取分页参数.
     *
     * @param obj 有分页参数的对象
     */
    public static Pageable toPageable(Object obj) {
        if (obj.getClass().isPrimitive()) {
            return Pageable.unpaged();
        }
        Tuple2<Integer, Integer> tuple2;
        try {
            tuple2 = getPageAndSize(obj);
        } catch (Throwable e) {
            throw GlobalException.wrap(e, "Unable to read paging parameters");
        }
        return PageRequest.of(tuple2.v1(), tuple2.v2());
    }

    /**
     * 获取mybatis plus分页参数.
     */
    @SuppressWarnings("unchecked")
    public <T> IPage<T> mpPage(Object obj) {
        Assert.notNull(obj, "mpPage obj must not be null");
        if (obj.getClass().isPrimitive()) {
            return (IPage<T>) EMPTY_MP_PAGE;
        }
        Tuple2<Integer, Integer> tuple2;
        try {
            tuple2 = getPageAndSize(obj);
        } catch (Throwable e) {
            throw GlobalException.wrap(e, "Unable to read paging parameters");
        }
        return Page.of(tuple2.v1(), tuple2.v2());
    }

    @SuppressWarnings("checkstyle:IllegalThrows")
    private static Tuple2<Integer, Integer> getPageAndSize(Object obj) throws Throwable {
        Class<?> clazz = obj.getClass();

        Integer page;
        Integer size;
        if (clazz.isRecord()) {
            MethodHandle pageHandle = LOOKUP.findVirtual(clazz, "page", MethodType.methodType(Integer.class));
            MethodHandle sizeHandle = LOOKUP.findVirtual(clazz, "size", MethodType.methodType(Integer.class));
            page = (Integer) pageHandle.invoke(obj);
            size = (Integer) sizeHandle.invoke(obj);
        } else if (obj instanceof me.ziyframework.web.common.dto.PageRequest pageRequest) {
            page = pageRequest.getPage();
            size = pageRequest.getSize();
        } else {
            page = (Integer) LOOKUP.findGetter(clazz, "page", int.class).invoke(obj);
            size = (Integer) LOOKUP.findGetter(clazz, "size", int.class).invoke(obj);
        }
        Assert.notNull(page, "page must not be null");
        Assert.notNull(size, "size must not be null");
        return Tuples.of(page, size);
    }
}
