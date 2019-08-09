package com.intuit.superglue.pipeline

import com.intuit.superglue.pipeline.producers.ScriptFileInput
import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}

class SourceTest extends FsSpec {

  "A parser Source" should "use FileInputConfigs to set up FileProviders" in {
    val files = Seq(
      "/path/to/fileA",
      "/path/to/fileB",
      "/path/to/fileC",
    )

    // Create a mock filesystem (see FsSpec).
    Fixture(files: _*) { implicit fs =>
      implicit val rootConfig: TypesafeConfig = ConfigFactory.parseString(
        """
          |com.intuit.superglue.pipeline.inputs.files = [
          | { base = "/"
          |   includes = [ "glob:**/*A*" ]
          |   kind = "kindA" },
          | { base = "/"
          |   includes = [ "glob:**/*B*" ] }
          |]
        """.stripMargin)

      // Create a Source and pass it the configs with file targets
      val source = new Source()

      // Collect all of the inputs generated by the Source
      val actualInputs = source.stream().toList
      val expectedInputs = List(
        ScriptFileInput(fs.getPath("/path/to/fileA"), "path/to/fileA", "kindA"),
      )
      assert(actualInputs == expectedInputs)
    }
  }
}