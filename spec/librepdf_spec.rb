
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

  context "given block argument to #initialize" do
    it "Only within the block, to be connected" do
      con = Connection.new('host' => @host, 'port' => @port) { |con|
        con.closed?.should == false
      }
      con.closed?.should == true
    end

    it "As an exception has occurred, it is similar to" do
      con = nil
      begin
        Connection.new('host' => @host, 'port' => @port) { |c|
          con = c
          raise RuntimeError
        }
      rescue; end
      closed = !! con.closed?
      closed.should == true
    end
  end

  context "when connect" do
    before do
      @con = Connection.new 'host' => @host, 'port' => @port
    end

    it "should not closed" do
      @con.closed?.should_not == true
    end

    it "should be able to load document" do
      inputUrl = "file://#{File.dirname(__FILE__)}/data/test.odt"
      doc = @con.load inputUrl
      doc.close rescue nil
    end

    context "when load document" do
      before do
        inputUrl = "file://#{File.dirname(__FILE__)}/data/test.odt"
        @doc = @con.load inputUrl
      end

      it "document should not closed" do
        @doc.closed?.should_not == true
      end

      it "should be able to convert to pdf" do
        outputUrl = "file://#{@dir}/test01.pdf"
        @doc.convert_pdf outputUrl
        File.file?("#{@dir}/test01.pdf").should == true
      end

      after do
        @doc.close
      end
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

