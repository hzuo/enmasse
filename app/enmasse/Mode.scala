package enmasse

sealed trait Mode
case object Map extends Mode
case object Reduce extends Mode