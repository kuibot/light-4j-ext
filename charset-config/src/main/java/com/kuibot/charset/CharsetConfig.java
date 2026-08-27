/*
 * Copyright (c) 2024 Kuibot.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kuibot.charset;

import com.networknt.config.Config;
import com.networknt.server.ModuleRegistry;

import java.util.List;
import java.util.Map;

/**
 * CharsetConfig is singleton, and it is loaded from charset.yml in the config folder.
 * Created by shaman on 13/10/24.
 *
 * @author Shaman Du
 * @since 2.1.37
 */
public class CharsetConfig {
    private static final String ENABLED = "enabled";
    private static final String CHARSET = "charset";
    private static final String CONTENT_TYPE_LIST = "contentTypeList";

    private Map<String, Object> mappedConfig;
    public static final String CONFIG_NAME = "charset";

    // In order to maintain consistency with the Light-4j core library, the default charset is set here to ISO-8859-1.
    private String charset = "ISO-8859-1";
    private boolean enabled;
    private List<String> contentTypeList;
    private static volatile CharsetConfig instance;

    private CharsetConfig() {
        this(CONFIG_NAME);
    }

    private CharsetConfig(String configName) {
        mappedConfig = Config.getInstance().getJsonMapConfig(configName);
        setConfigData();
    }

    public static CharsetConfig load() {
        return load(CONFIG_NAME);
    }

    public static CharsetConfig load(String configName) {
        if (CONFIG_NAME.equals(configName)) {
            Map<String, Object> mappedConfig = Config.getInstance().getJsonMapConfig(configName);
            if (instance != null && instance.getMappedConfig() == mappedConfig) {
                return instance;
            }
            synchronized (CharsetConfig.class) {
                mappedConfig = Config.getInstance().getJsonMapConfig(configName);
                if (instance != null && instance.getMappedConfig() == mappedConfig) {
                    return instance;
                }
                instance = new CharsetConfig(configName);
                ModuleRegistry.registerModule(configName, CharsetConfig.class.getName(), Config.getNoneDecryptedInstance().getJsonMapConfigNoCache(configName), null);
                return instance;
            }
        }
        return new CharsetConfig(configName);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCharset() {
        return charset;
    }

    public List<String> getContentTypeList() {
        return contentTypeList;
    }

    public Map<String, Object> getMappedConfig() {
        return mappedConfig;
    }

    private void setConfigData() {
        if (mappedConfig == null) {
            return;
        }
        Object object = mappedConfig.get(ENABLED);
        if (object != null) enabled = Config.loadBooleanValue(ENABLED, object);
        object = mappedConfig.get(CHARSET);
        if (object != null) charset = (String) object;
        object = mappedConfig.get(CONTENT_TYPE_LIST);
        if (object != null) contentTypeList = (List<String>) object;
    }
}
