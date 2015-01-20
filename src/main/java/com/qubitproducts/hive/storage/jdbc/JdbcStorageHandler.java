/*
 * Copyright 2012-2014 Qubit Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qubitproducts.hive.storage.jdbc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.HiveMetaHook;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.HiveStorageHandler;
import org.apache.hadoop.hive.ql.plan.TableDesc;
import org.apache.hadoop.hive.ql.security.authorization.HiveAuthorizationProvider;
import org.apache.hadoop.hive.serde2.SerDe;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.OutputFormat;

import com.qubitproducts.hive.storage.jdbc.conf.JdbcStorageConfigManager;

import java.util.Map;
import java.util.Properties;

public class JdbcStorageHandler  extends org.apache.hcatalog.mapreduce.HCatStorageHandler {

    private Configuration conf;


    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }


    @Override
    public Configuration getConf() {
        return this.conf;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends InputFormat> getInputFormatClass() {
        return JdbcInputFormat.class;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends OutputFormat> getOutputFormatClass() {
        return JdbcOutputFormat.class;
    }


    @Override
    public Class<? extends SerDe> getSerDeClass() {
        return JdbcSerDe.class;
    }


    @Override
    public HiveMetaHook getMetaHook() {
        return null;
    }

/*
    @Override
    public void configureTableJobProperties(TableDesc tableDesc, Map<String, String> jobProperties) {
        Properties properties = tableDesc.getProperties();
        JdbcStorageConfigManager.copyConfigurationToJob(properties, jobProperties);
    }
*/

    @Override
    public void configureInputJobProperties(TableDesc tableDesc, Map<String, String> jobProperties) {
        Properties properties = tableDesc.getProperties();
        JdbcStorageConfigManager.copyConfigurationToJob(properties, jobProperties);
    }


    @Override
    public void configureOutputJobProperties(TableDesc tableDesc, Map<String, String> jobProperties) {
        // Nothing to do here...
    }


    @Override
    public HiveAuthorizationProvider getAuthorizationProvider() throws HiveException {
        return null;
    }
	
	public void configureJobConf(org.apache.hadoop.hive.ql.plan.TableDesc tableDesc,org.apache.hadoop.mapred.JobConf jobConf) {
	}


}
