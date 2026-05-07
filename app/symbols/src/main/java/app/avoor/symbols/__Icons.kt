package app.avoor.symbols

import androidx.compose.ui.graphics.vector.ImageVector
import app.avoor.symbols.icons.Add
import app.avoor.symbols.icons.ArrowBack
import app.avoor.symbols.icons.ArrowForward
import app.avoor.symbols.icons.Check
import app.avoor.symbols.icons.Close
import app.avoor.symbols.icons.Error
import app.avoor.symbols.icons.Focus
import app.avoor.symbols.icons.Info
import app.avoor.symbols.icons.ModeHeat
import app.avoor.symbols.icons.Plancoin
import app.avoor.symbols.icons.Search
import app.avoor.symbols.icons.Shuffle
import app.avoor.symbols.icons.Streak
import app.avoor.symbols.icons.Target
import app.avoor.symbols.icons.Warning
import app.avoor.symbols.icons.Yinyang
import kotlin.collections.List as ____KtList

object Icons

private var __AllIcons: ____KtList<ImageVector>? = null

val Icons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Add, ArrowBack, ArrowForward, Check, Close, Error, Focus,
        Info, ModeHeat, Plancoin, Search, Shuffle, Streak, Target, Warning, Yinyang)
    return __AllIcons!!
  }
