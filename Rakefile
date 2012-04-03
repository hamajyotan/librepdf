#!/usr/bin/env rake
require "bundler/gem_tasks"

require 'ant'

classpath = [
  File.dirname(__FILE__) + '/java/lib/juh.jar',
  File.dirname(__FILE__) + '/java/lib/jurt.jar',
  File.dirname(__FILE__) + '/java/lib/ridl.jar',
  File.dirname(__FILE__) + '/java/lib/unoil.jar',
  ENV['JRUBYJAR'],
].compact.join(':')

desc "Build librepdf jar (required ant)"
task :jar do
  ant.mkdir :dir => File.dirname(__FILE__) + '/java/bin'
  ant.javac :srcdir => File.dirname(__FILE__) + '/java/src', :destdir => File.dirname(__FILE__) + '/java/bin', :includeantruntime => 'no', :classpath => classpath do
    compilerarg :value => '-Xlint:unchecked'
  end
  ant.jar :jarfile => File.dirname(__FILE__) + '/lib/librepdf/java/librepdf.jar', :filesetmanifest => 'mergewithoutmain' do
    fileset :dir => File.dirname(__FILE__) + '/java/bin', :includes => '**/*.class'
    fileset :dir => File.dirname(__FILE__) + '/java/src', :includes => '**/*.java'
  end
  ant.copy :todir => File.dirname(__FILE__) + '/lib/librepdf/java/' do
    fileset :dir => File.dirname(__FILE__) + '/java/lib', :includes => '*.jar'
  end
  ant.delete :dir => File.dirname(__FILE__) + '/java/bin'
end

begin
  gem 'rspec', '>= 2.0.0'
  require 'rspec'
  require 'rspec/core/rake_task'
rescue LoadError
  puts <<EOS
To use rspec for testing you must install rspec gem:
    gem install rspec

EOS
  exit 0
end

desc "Run the specs under spec/*"
RSpec::Core::RakeTask.new do |t|
  t.pattern = './spec/**/*_spec.rb'
  t.rspec_opts = '-c -f d'
end

task :build => :jar
task :spec => :jar
task :default => :spec

