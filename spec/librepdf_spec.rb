
require File.dirname(__FILE__) + '/spec_helper.rb'

include Librepdf

require 'fileutils'
require 'tmpdir'

# Precondition.
#
# Required start libreoffice as a service.
#
describe Connection do
  before(:all) do
    @host = ENV['SOFFICE_HOST'] || '127.0.0.1'
    @port = ENV['SOFFICE_PORT'] || 8100

    @dir = Dir.mktmpdir
  end

  it "Connection should be able to create a instance" do
    @con = Connection.new 'host' => @host, 'port' => @port
  end

  context "when connect" do
    before do
      @con = Connection.new 'host' => @host, 'port' => @port
    end

    it "should be able to load document" do
      inputUrl = "file://#{File.dirname(__FILE__) + '/data/test.odt'}"
      doc = @con.load inputUrl
    end

    after do
      @con.close
    end
  end

  after(:all) do
    FileUtils.rm_r @dir
    @con.close if @con and !@con.closed?
  end
end

