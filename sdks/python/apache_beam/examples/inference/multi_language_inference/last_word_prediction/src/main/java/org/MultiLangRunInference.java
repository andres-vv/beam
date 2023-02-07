package org;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.python.PythonExternalTransform;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.values.PCollection;

public class MultiLangRunInference {
    public interface MultiLanguageOptions extends PipelineOptions {

        @Description("Path to an input file that contains labels and pixels to feed into the model")
        @Required
        String getInputFile();

        void setInputFile(String value);

        @Description("Path to a stored model.")
        @Required
        String getModelPath();

        void setModelPath(String value);

        @Description("Path to an input file that contains labels and pixels to feed into the model")
        @Required
        String getOutputFile();

        void setOutputFile(String value);

        @Description("Name of the model on HuggingFace.")
        @Required
        String getModelName();

        void setModelName(String value);

        @Description("Port number of the expansion service.")
        @Required
        String getPort();

        void setPort(String value);
    }

    public static void main(String[] args) {

        MultiLanguageOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
                .as(MultiLanguageOptions.class);

        Pipeline p = Pipeline.create(options);
        PCollection<String> input = p.apply("Read Input", TextIO.read().from(options.getInputFile()));
        

        List<String> local_packages=new ArrayList<String>();
        local_packages.add("transformers==4.26.0");
        local_packages.add("torch==1.13.1");
        local_packages.add("/Users/andresvervaecke/projects/client-work/Beam/beam/sdks/python/apache_beam/examples/inference/multi_language_inference/multi_language_custom_transform/dist/multi-language-custom-transform-0.1.tar.gz"); 

        List<String> packages=new ArrayList<String>();  
        input.apply("Predict", PythonExternalTransform.<PCollection<String>, PCollection<String>>from(
                "multi_language_custom_transform.composite_transform.InferenceTransform")
                .withKwarg("model", options.getModelName())
                .withKwarg("model_path", options.getModelPath())
                .withExtraPackages(local_packages)
                )
                .apply("Write Output", TextIO.write().to(options.getOutputFile()));

        p.run().waitUntilFinish();
    }
}
