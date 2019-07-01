package org.ryanyuan;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.ryanyuan.io.WindowedFilenamePolicy;
import org.ryanyuan.utils.DurationUtils;


/**
 * Dataflow streaming pipeline to read messages from PubSub and write the
 * payload to Google Cloud Storage
 */
public class PubSubToCloudStoragePipeline {

    /**
     * Main entry point for executing the pipeline.
     * @param args  The command-line arguments to the pipeline.
     */
    public static void main(String[] args) {
        PipelineOptionsFactory.register(PubSubToCloudStorageOptions.class);
        PubSubToCloudStorageOptions options = PipelineOptionsFactory.fromArgs(args)
                .withValidation()
                .as(PubSubToCloudStorageOptions.class);

        run(options);
    }

    /**
     * Runs the pipeline with the supplied options.
     *
     * @param options The execution parameters to the pipeline.
     * @return  The result of the pipeline execution.
     */
    public static PipelineResult run(PubSubToCloudStorageOptions options) {
        Pipeline pipeline = Pipeline.create(options);

        pipeline.apply("Read from Pub/Sub",
                        PubsubIO.readStrings().fromSubscription(options.getInputSubscription()))
                .apply(String.format("Apply %s window", options.getWindowDuration()),
                        Window.into(FixedWindows.of(DurationUtils.parseDuration(options.getWindowDuration()))))
                .apply("Write to GCS",
                        TextIO.write()
                                .withWindowedWrites()
                                .withNumShards(options.getNumShards())
                                .to(new WindowedFilenamePolicy(
                                        options.getOutputDirectory(),
                                        options.getOutputFilenamePrefix(),
                                        options.getOutputShardTemplate(),
                                        options.getOutputFilenameSuffix()))
                                .withTempDirectory(ValueProvider.NestedValueProvider.of(
                                    options.getOutputDirectory(),
                                    (SerializableFunction<String, ResourceId>) input ->
                                        FileBasedSink.convertToFileResourceIfPossible(input))));

        return pipeline.run();
    }
}