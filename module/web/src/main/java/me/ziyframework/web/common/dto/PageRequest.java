package me.ziyframework.web.common.dto;

import lombok.Data;

/**
 * 分页请求.
 * created in 2025-08
 *
 * @author ziy
 */
@Data
public class PageRequest {

    /**
     * 页码.
     */
    private int page;

    /**
     * 页大小.
     */
    private int size;
}
