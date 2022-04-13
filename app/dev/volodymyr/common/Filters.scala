package dev.volodymyr.common

import play.api.http.DefaultHttpFilters

import javax.inject.Inject

class Filters @Inject() (logging: LoggingFilter)
  extends DefaultHttpFilters(logging)
