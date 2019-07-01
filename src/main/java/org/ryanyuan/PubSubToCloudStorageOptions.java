package org.ryanyuan;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.options.*;

/**
 * Options supported by the pipeline.
 *
 * <p>Inherits standard configuration options.</p>
 */
public interface PubSubToCloudStorageOptions extends DataflowPipelineOptions {
    @Description("The Cloud Pub/Sub subscription to read from.")
    @Required
    ValueProvider<String> getInputSubscription();
    void setInputSubscription(ValueProvider<String> value);

    @Description("Pub/Sub topic to read the input from")
    ValueProvider<String> getInputTopic();
    void setInputTopic(ValueProvider<String> value);

    @Description(
            "This determines whether the template reads from " + "a pub/sub subscription or a topic")
    @Default.Boolean(true)
    Boolean getUseSubscription();
    void setUseSubscription(Boolean value);

    @Description("The directory to output files to. Must end with a slash.")
    @Required
    ValueProvider<String> getOutputDirectory();
    void setOutputDirectory(ValueProvider<String> value);

    @Description("The filename prefix of the files to write to.")
    @Default.String("output")
    @Required
    ValueProvider<String> getOutputFilenamePrefix();
    void setOutputFilenamePrefix(ValueProvider<String> value);

    @Description("The suffix of the files to write.")
    @Default.String("")
    ValueProvider<String> getOutputFilenameSuffix();
    void setOutputFilenameSuffix(ValueProvider<String> value);

    @Description("The shard template of the output file. Specified as repeating sequences "
            + "of the letters 'S' or 'N' (example: SSS-NNN). These are replaced with the "
            + "shard number, or number of shards respectively")
    @Default.String("W-P-SS-of-NN")
    ValueProvider<String> getOutputShardTemplate();
    void setOutputShardTemplate(ValueProvider<String> value);

    @Description("The maximum number of output shards produced when writing.")
    @Default.Integer(1)
    Integer getNumShards();
    void setNumShards(Integer value);

    @Description("The window duration in which data will be written. Defaults to 5m. "
            + "Allowed formats are: "
            + "Ns (for seconds, example: 5s), "
            + "Nm (for minutes, example: 12m), "
            + "Nh (for hours, example: 2h).")
    @Default.String("5m")
    String getWindowDuration();
    void setWindowDuration(String value);
}