package io.github.leonhardtdavid.gettingtweetsagent.conf

import com.typesafe.config.Config

class ApplicationConf(config: Config) {

  // scalastyle:off object.name
  // scalastyle:off public.methods.have.type

  object services {
    private val conf = config.getConfig("services")

    object interests {
      private val interestsConf = conf.getConfig("interests")

      def url = interestsConf.getString("url")
      def gap = interestsConf.getDuration("gap")
    }

    object stream {
      private val streamConf = conf.getConfig("stream")

      def addUrl = streamConf.getString("add-url")
      def runUrl = streamConf.getString("run-url")
    }
  }

}
