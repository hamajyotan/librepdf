# Librepdf

This software changes a document into PDF by Libreoffice assistance.

## Installation

Add this line to your application's Gemfile:

    gem 'librepdf'

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install librepdf

## Set before use

### Start libreoffice as a service without GUI.

    $ /opt/libreoffice3.5/program/soffice.bin \
        --accept="socket,host=127.0.0.1,port=8100,tcpNoDelay=1;urp;" \
        --headless \
        --invisible \
        --nodefault \
        --nofirststartwizard \
        --nolockcheck \
        --nologo \
        --norestore \
        &

### When multiinstance, required change user profile location.

    $ /opt/libreoffice3.5/program/soffice.bin \
        --accept="socket,host=127.0.0.1,port=8100,tcpNoDelay=1;urp;" \
        --headless \
        --invisible \
        --nodefault \
        --nofirststartwizard \
        --nolockcheck \
        --nologo \
        --norestore \
        -env:UserInstallation=file:///home/foo/.libreoffice/3 \
        &

## Usage

### Simple example

    require 'librepdf'
   
    con = Librepdf::Connection.new 'host' => '127.0.0.1', 'port' => 8100
    doc = con.load 'file:///foo/bar/baz.doc'
    doc.convert_pdf 'file:///foo/bar/baz.pdf'
    doc.close
    con.close

### Safety usage

    require 'librepdf'
   
    Librepdf::Connection.new('host' => '127.0.0.1', 'port' => 8100) { |con|
      con.load('file:///foo/bar/baz.doc') { |doc|
        doc.convert_pdf 'file:///foo/bar/baz.pdf'
      }
    }

### Password file

    require 'librepdf'
   
    Librepdf::Connection.new('host' => '127.0.0.1', 'port' => 8100) { |con|
      con.load('file:///foo/bar/baz.doc', 'Password' => '@SecretP@ssW0rd') { |doc|
        doc.convert_pdf 'file:///foo/bar/baz.pdf'
      }
    }

### Pickup page

    require 'librepdf'
   
    Librepdf::Connection.new('host' => '127.0.0.1', 'port' => 8100) { |con|
      con.load('file:///foo/bar/baz.doc') { |doc|
        doc.convert_pdf 'file:///foo/bar/baz1.pdf', 'FilterData' => { 'PageRange' => '1-1' }
        doc.convert_pdf 'file:///foo/bar/baz2.pdf', 'FilterData' => { 'PageRange' => '2-2' }
        doc.convert_pdf 'file:///foo/bar/baz3.pdf', 'FilterData' => { 'PageRange' => '3-3' }
      }
    }

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

