# -*- encoding: utf-8 -*-
require File.expand_path('../lib/librepdf/version', __FILE__)

Gem::Specification.new do |gem|
  gem.authors       = ["hamajyotan"]
  gem.email         = ["hamajyotan@gmail.com"]
  gem.description   = %q{This software changes a document into PDF by Libreoffice assistance under JRuby.}
  gem.summary       = %q{This software changes a document into PDF by Libreoffice assistance under JRuby.}
  gem.homepage      = ""

  gem.files         = Dir['lib/librepdf/java/*.jar'] << `git ls-files`.split($\)
  gem.executables   = gem.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  gem.test_files    = gem.files.grep(%r{^(test|spec|features)/})
  gem.name          = "librepdf"
  gem.require_paths = ["lib"]
  gem.version       = Librepdf::VERSION

  gem.add_development_dependency 'rspec', '>= 2.0.0'
end

