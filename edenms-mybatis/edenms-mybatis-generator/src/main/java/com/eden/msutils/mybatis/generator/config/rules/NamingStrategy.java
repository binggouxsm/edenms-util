/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.eden.msutils.mybatis.generator.config.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.eden.msutils.mybatis.generator.config.ConstVal;
import com.eden.msutils.mybatis.generator.utils.StringUtils;


/**
 * 从数据库表到文件的命名策略
 *
 * @author YangHu, tangguo
 * @since 2016/8/30
 */
public enum NamingStrategy {
    /**
     * 不做任何改变，原样输出
     */
    no_change,
    /**
     * 下划线转驼峰命名
     */
    underline_to_camel;


}
