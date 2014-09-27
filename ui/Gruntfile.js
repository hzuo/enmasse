module.exports = function (grunt) {
  "use strict";

  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    clean: {
      build: {
        src: [ 'build' ]
      }
    },
    copy: {
      build: {
        files: [
          {
            src: 'bower_components/normalize-css/normalize.css',
            dest: 'build/styles/normalize.css'
          },
          {
            src: 'bower_components/pure/grids.css',
            dest: 'build/styles/grids.css'
          },
          {
            src: 'bower_components/DataTables/media/css/jquery.dataTables.css',
            dest: 'build/styles/jquery.dataTables.css'
          },

          {
            expand: true,
            cwd: 'lib',
            src: 'styles/**',
            dest: 'build'
          },
          {
            expand: true,
            cwd: 'app',
            src: '**/*.html',
            dest: 'build'
          },
          {
            expand: true,
            cwd: 'app',
            src: 'images/**',
            dest: 'build'
          }
        ]
      },
      dist: {
        files: [
          {
            expand: true,
            cwd: 'build',
            src: '**',
            dest: '../public'
          }
        ]
      }
    },
    bower_concat: {
      build: {
        exclude: ['normalize-css', 'pure', 'dcjs'],
        dest: 'build/scripts/bower.js'
      }
    },
    ts: {
      options: {
        verbose: true,
        sourceMap: false, // TODO
        htmlModuleTemplate: "UNUSED",
        htmlVarTemplate: "UNUSED"
      },
      build: {
        src: 'app/scripts/**/*.ts',
        reference: 'app/scripts/reference.ts',
        out: 'build/scripts/app.js'
      }
    },
    less: {
      options: {
        paths: 'app/styles',
        sourceMap: false // TODO
      },
      build: {
        files: [
          {
            expand: true,
            cwd: 'app',
            src: 'styles/**/*.less',
            dest: 'build',
            ext: '.css'
          }
        ]
      }
    },
    concat: {
      buildjs: {
        options: {
          separator: ';'
        },
        files: {
          'build/scripts/lib.js': 'lib/scripts/**/*.js',
          'build/scripts/<%= pkg.name %>.js': [
            'build/scripts/bower.js',
            'build/scripts/lib.js',
            'build/scripts/app.js'
          ]
        }
      },
      buildcss: {
        files: {
          'build/styles/<%= pkg.name %>.css': ['build/styles/**/*.css']
        }
      }
    },
    uglify: {
      options: {
        sourceMap: false // TODO
      },
      build: {
        src: 'build/scripts/<%= pkg.name %>.js',
        dest: 'build/scripts/<%= pkg.name %>.js'
      }
    },
    jshint: {
      options: {
        globals: {
          module: true
        }
      },
      gruntfile: {
        src: 'Gruntfile.js'
      }
    },
    cssmin: {
      build: {
        src: 'build/styles/<%= pkg.name %>.css',
        dest: 'build/styles/<%= pkg.name %>.css'
      }
    },
    watch: {
      inside: {
        files: ['app/**', 'lib/**'],
        tasks: ['dev']
      }
    }
  });

  grunt.registerTask('build', [
    'clean',
    'jshint',
    'copy:build',
    'bower_concat:build',
    'ts:build',
    'less:build',
    'concat:buildjs',
    'concat:buildcss'
  ]);
  grunt.registerTask('min', ['uglify:build', 'cssmin:build']);

  grunt.registerTask('dev', ['build', 'copy:dist']);
  grunt.registerTask('prod', ['build', 'min', 'copy:dist']);

};
