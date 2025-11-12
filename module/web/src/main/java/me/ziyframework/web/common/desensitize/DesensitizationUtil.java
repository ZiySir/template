package me.ziyframework.web.common.desensitize;

import com.palantir.logsafe.SafeArg;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import lombok.Getter;
import me.ziyframework.web.common.exception.GlobalException;

/**
 * 脱敏工具类，用于代码中脱敏.<br />
 * created on 2025-02
 * @author ziy
 */
public final class DesensitizationUtil {

    private DesensitizationUtil() {}

    public static String slide(String value, int left, int right) {
        return slide(value, left, right, '*', false);
    }

    /**
     * 滑动脱敏.
     * @param value 待脱敏数据
     * @param left 左边保留长度
     * @param right 右边保留长度
     * @param mask 遮罩字符
     * @param reverse 是否反转
     * @return 脱敏后的数据
     */
    public static String slide(String value, int left, int right, char mask, boolean reverse) {
        if (!checkSlide(value, left, right)) {
            return value;
        }
        final int length = value.length();
        StringBuilder strBuilder = new StringBuilder(length);
        final int rightInclude = length - right - 1;
        for (int i = 0; i < length; i++) {
            if (i < left || i > rightInclude) {
                strBuilder.append(reverse ? mask : value.charAt(i));
                continue;
            }
            strBuilder.append(reverse ? value.charAt(i) : mask);
        }
        return strBuilder.toString();
    }

    /**
     * 正则表达式替换脱敏.
     * @param value 待脱敏数据
     * @param regex 正则表达式
     * @param replace 替换字符串
     * @return 脱敏后的数据
     */
    public static String regex(String value, String regex, String replace) {
        if (value == null || value.isEmpty() || regex == null || replace == null) {
            return value;
        }
        return value.replaceAll(regex, replace);
    }

    public static String index(String value, String... indexes) {
        return index(value, '*', false, indexes);
    }

    /**
     * 基于下标脱敏.
     * @param value 待脱敏数据
     * @param mask 遮掩字符
     * @param reverse 是否反转
     * @param indexes 下标集(include-include, include-, -include, include)
     * @return 脱敏后的数据
     */
    public static String index(String value, char mask, boolean reverse, String... indexes) {
        if (value == null || indexes.length == 0) {
            return value;
        }
        // 解析并重叠优化
        IndexRange[] ranges = getIndexRanges(value.length(), indexes);
        // 然后对这些区间进行脱敏/反转脱敏
        StringBuilder strBuilder = new StringBuilder(value.length());
        for (int i = 0, r = 0; i < value.length(); i++) {
            if (ranges[r].isOutside(i) == reverse) {
                strBuilder.append(mask);
            } else {
                strBuilder.append(value.charAt(i));
            }
            if (i > ranges[r].endInclude) {
                r++;
            }
        }

        return strBuilder.toString();
    }

    private static boolean checkSlide(String value, int left, int right) {
        final int length;
        return value != null && left >= 0 && right >= 0 && left < (length = value.length()) && right < length;
    }

    private static IndexRange[] getIndexRanges(int valueLength, String... indexes) {
        IndexRange[] ranges = Arrays.stream(indexes)
                .map(indexRange -> parseIndexRange(valueLength, indexRange))
                .distinct()
                .sorted(Comparator.comparingInt(IndexRange::getStartInclude))
                .toArray(IndexRange[]::new);
        IndexRange current;
        IndexRange last;
        for (int i = 1; i < ranges.length; i++) {
            current = ranges[i];
            last = ranges[i - 1];
            if (current.startInclude < last.endInclude) {
                // 存在重合,合并
                ranges[i] = null;
                if (current.endInclude > last.endInclude) {
                    // 不完全重合，扩展到最大范围
                    last.endInclude = current.endInclude;
                }
            }
        }
        return Arrays.stream(ranges).filter(Objects::nonNull).toArray(IndexRange[]::new);
    }

    /**
     * 解析IndexRange.<br/>
     * example: 0-9, 0-, -9, 4
     */
    private static IndexRange parseIndexRange(int valueLength, String indexRange) {
        if (indexRange == null || indexRange.isEmpty() || indexRange.contains(" ")) {
            throw new GlobalException("index range is illegal", SafeArg.of("indexRange", indexRange));
        }
        final int indexRangeLength = indexRange.length();
        final int splitIndex = indexRange.indexOf("-");
        IndexRange range;
        if (splitIndex == -1) {
            range = new IndexRange(Integer.parseInt(indexRange));
        } else if (splitIndex == 0) {
            range = new IndexRange(0, Integer.parseInt(indexRange.substring(1)));
        } else if (splitIndex == indexRangeLength - 1) {
            range = new IndexRange(Integer.parseInt(indexRange.substring(0, splitIndex)), valueLength - 1);
        } else {
            range = new IndexRange(
                    Integer.parseInt(indexRange.substring(0, splitIndex)),
                    Integer.parseInt(indexRange.substring(splitIndex + 1)));
        }
        return range;
    }

    @Getter
    private static class IndexRange {

        private int startInclude;

        private int endInclude;

        IndexRange(int startInclude, int endInclude) {
            if (startInclude < 0 || endInclude < 0 || startInclude > endInclude) {
                throw new GlobalException(
                        "index range is illegal", SafeArg.of("start", startInclude), SafeArg.of("end", endInclude));
            }
            this.startInclude = startInclude;
            this.endInclude = endInclude;
        }

        IndexRange(int index) {
            this(index, index);
        }

        boolean isOutside(int index) {
            return index < startInclude || index > endInclude;
        }

        @Override
        public int hashCode() {
            return Objects.hash(startInclude, endInclude);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof IndexRange indexRange) {
                return indexRange.endInclude == this.endInclude && indexRange.startInclude == this.startInclude;
            }
            return false;
        }
    }
}
