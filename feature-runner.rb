#!/usr/bin/env ruby

require 'optparse'

class RunningOpt

  def initialize(argv)
    @options = {}
    @argv = argv

    @opt_parser = OptionParser.new do |opts|
      opts.banner = "Usage: ruby feature-runner.rb [options]"
      opts.separator "Options:"

      opts.on("-h", "--help", "Help messages") do |help|
        @options[:help] = help
      end

      opts.on("--rebuild-app=false", "Rebuild target-app (false is default)") do |rebuild_app|
        @options[:rebuild_app] = rebuild_app
      end

      opts.on("--rebuild-test=false", "Rebuild test (false is default)") do |rebuild_test|
        @options[:rebuild_test] = rebuild_test
      end

      opts.on("--hang-on=false", "Hang on after testing (false is default)") do |hang_on|
        @options[:hang_on] = hang_on
      end

      opts.on("--feature-tag=@launching_portal", "Only testing for specified feature (all tests running is default)") do |feature_tag|
        @options[:feature_tag] = feature_tag
      end

    end

    @opt_parser.parse!(argv)
  end

  def get(key)
    @options[key]
  end

  def print_usage
    puts @opt_parser
  end

end

if __FILE__ == $0
  running_opt = RunningOpt.new(ARGV)

  if running_opt.get(:help) != nil
    running_opt.print_usage
    exit(0)
  end

  rebuild_app = ""
  if running_opt.get(:rebuild_app) == 'true'
    rebuild_app = "gradle installDebug && "
  end

  rebuild_test = ""
  if running_opt.get(:rebuild_test) == 'true'
    rebuild_test = "gradle iDAT && "
  end

  hang_on = ""
  if running_opt.get(:hang_on) == 'true'
    hang_on = "-e hangon true "
  end

  feature = running_opt.get(:feature_tag)
  if feature != nil
    feature = "-e tags #{feature} "
  else
    feature = "-e features \"features\" "
  end

  adb_shell = "adb shell am instrument -w -r #{hang_on}#{feature} net.poksion.chorong.android.samples.test/net.poksion.chorong.android.samples.test.Instrumentation"

  exec("#{rebuild_app}#{rebuild_test}#{adb_shell}")
end

