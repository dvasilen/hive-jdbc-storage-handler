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

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.HiveInputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qubitproducts.hive.storage.jdbc.dao.DatabaseAccessor;
import com.qubitproducts.hive.storage.jdbc.dao.DatabaseAccessorFactory;

import java.io.IOException;

public class JdbcInputFormat extends HiveInputFormat<LongWritable, MapWritable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcInputFormat.class);
    private DatabaseAccessor dbAccessor = null;


    /**
     * {@inheritDoc}
     */
    @Override
    public RecordReader<LongWritable, MapWritable>
        getRecordReader(InputSplit split, JobConf job, Reporter reporter) throws IOException {

        if (!(split instanceof JdbcInputSplit)) {
            throw new RuntimeException("Incompatible split type " + split.getClass().getName() + ".");
        }

        return new JdbcRecordReader(job, (JdbcInputSplit) split);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public InputSplit[] getSplits(JobConf job, int numSplits) throws IOException {
		if(numSplits < 1 ) {
			numSplits = 1;
		}
        try {
            LOGGER.debug("Creating {} input splits", numSplits);
            if (dbAccessor == null) {
                dbAccessor =  DatabaseAccessorFactory.getAccessor(job);
            }

            int numRecords = dbAccessor.getTotalNumberOfRecords(job);
            int numRecordsPerSplit = numRecords / numSplits;
            int numSplitsWithExtraRecords = numRecords % numSplits;

            LOGGER.debug("Num records = {}", numRecords);
            InputSplit[] splits = new InputSplit[numSplits];
            Path[] tablePaths = FileInputFormat.getInputPaths(job);

            int offset = 0;
            for (int i = 0; i < numSplits; i++) {
                int numRecordsInThisSplit = numRecordsPerSplit;
                if (i < numSplitsWithExtraRecords) {
                    numRecordsInThisSplit++;
                }

                splits[i] = new JdbcInputSplit(numRecordsInThisSplit, offset, tablePaths[0]);
                offset += numRecordsInThisSplit;
            }

            return splits;
        }
        catch (Exception e) {
            LOGGER.error("Error while splitting input data.", e);
            throw new IOException(e);
        }
    }


    /**
     * For testing purposes only
     *
     * @param dbAccessor
     *            DatabaseAccessor object
     */
    public void setDbAccessor(DatabaseAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;
    }

}
