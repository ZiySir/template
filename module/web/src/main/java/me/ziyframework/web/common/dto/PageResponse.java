package me.ziyframework.web.common.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * 分页结果封装类.<br/>
 * created on 2025-03
 *
 * @author ziy
 */
@AllArgsConstructor
@Getter
public class PageResponse<T> implements Serializable {

    public static final PageResponse<?> EMPTY = new PageResponse<>(0, 1, 0, Collections.emptyList());

    private long total;

    private long page;

    private long size;

    private Collection<T> items;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 获取一个空的分页结果.
     */
    @SuppressWarnings("unchecked")
    public static <R> PageResponse<R> empty() {
        return (PageResponse<R>) EMPTY;
    }

    public static <T, R> PageResponse<R> transform(PageResponse<T> pageResponse, Function<T, R> transform) {
        return new PageResponse<>(
                pageResponse.getTotal(),
                pageResponse.getPage(),
                pageResponse.getSize(),
                pageResponse.getItems().stream().map(transform).toList());
    }

    /**
     * <b>mybatis plus</b>: 将 {@link IPage} 转换为 {@link PageResponse}.<br/>
     * 不做任何元素转换.
     */
    public static <R> PageResponse<R> transform(IPage<R> page) {
        return new PageResponse<>(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                Objects.requireNonNullElse(page.getRecords(), Collections.emptyList()));
    }

    /**
     * <b>Spring Data</b>：将{@link Page} 转换为 {@link PageResponse}.
     */
    public static <R> PageResponse<R> transform(Page<R> page) {
        return new PageResponse<>(
                page.getTotalElements(), page.getNumber() + 1, page.getNumberOfElements(), page.getContent());
    }

    /**
     * <b>mybatis plus</b>: 将 {@link IPage} 转换为 {@link PageResponse}.<br/>
     * 提供依次转换元素的函数.
     */
    public static <T, R> PageResponse<R> transform(IPage<T> page, Function<T, R> transform) {
        IPage<R> convertPage = page.convert(transform);
        return transform(convertPage);
    }

    /**
     * <b>Spring Data</b>：将{@link Page} 转换为 {@link PageResponse}.<br/>
     * 提供依次转换元素的函数.
     */
    public static <T, R> PageResponse<R> transform(Page<T> page, Function<T, R> transform) {
        Page<R> mapPage = page.map(transform);
        return transform(mapPage);
    }

    /**
     * 将 {@link PageResponse} 转换为 {@link PageResponse}.
     */
    public static <T, R> PageResponse<R> transformCollection(
            PageResponse<T> pageResponse, Function<Collection<T>, Collection<R>> transform) {
        return new PageResponse<>(
                pageResponse.getTotal(),
                pageResponse.getPage(),
                pageResponse.getSize(),
                transform.apply(pageResponse.getItems()));
    }

    /**
     * <b>mybatis plus</b>: 将 {@link Page} 转换为 {@link PageResponse}.<br/>
     * 提供整体转换的函数.
     */
    public static <T, R> PageResponse<R> transformCollection(
            IPage<T> page, Function<Collection<T>, Collection<R>> transform) {
        if (page.getRecords() == null) {
            return new PageResponse<>(page.getTotal(), page.getCurrent() - 1, page.getSize(), Collections.emptyList());
        }
        return new PageResponse<>(
                page.getTotal(), page.getCurrent() - 1, page.getSize(), transform.apply(page.getRecords()));
    }

    /**
     * <b>Spring Data</b>: 将 {@link Page} 转换为 {@link PageResponse}.<br/>
     * 提供整体转换的函数.
     */
    public static <T, R> PageResponse<R> transformCollection(
            Page<T> page, Function<Collection<T>, Collection<R>> transform) {
        page.getContent();
        return new PageResponse<>(
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getNumberOfElements(),
                transform.apply(page.getContent()));
    }
}
