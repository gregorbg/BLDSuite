package com.suushiemaniac.cubing.bld.model.puzzledef

import com.suushiemaniac.cubing.bld.util.CollectionUtil.headWithTail
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import java.io.File
import java.io.InputStream

data class CommandMap(val commands: Map<String, List<List<String>>>): Map<String, List<List<String>>> by commands {
    companion object {
        const val LINE_COMMAND_TERMINATOR = "End"
        private val EXTRA_COMMANDS = listOf("Solved", "Move", "ParityDependency", "Lettering", "Orientation", "CompositeMove")

        fun groupByCommand(lines: List<String>): CommandMap {
            val cmdGroups = mutableMapOf<String, MutableList<List<String>>>()
            val usefulLines = lines.filter { it.isNotBlank() }

            for ((i, ln) in usefulLines.withIndex()) {
                val (cmd, args) = ln.splitAtWhitespace().headWithTail()
                val data = args.toMutableList()

                if (cmd in EXTRA_COMMANDS) {
                    data.add(untilNextEnd(usefulLines, i + 1).joinToString("\n"))
                }

                cmdGroups.getOrPut(cmd) { mutableListOf() }.add(data)
            }

            return CommandMap(cmdGroups)
        }

        private fun untilNextEnd(lines: List<String>, currPointer: Int): List<String> {
            for ((i, ln) in lines.withIndex()) {
                if (i > currPointer && ln.trim().toLowerCase() == "end") {
                    return lines.subList(currPointer, i)
                }
            }

            return lines.drop(currPointer)
        }

        fun loadFileStream(kFile: InputStream) = groupByCommand(kFile.reader().readLines())
        fun loadFile(kFile: File) = groupByCommand(kFile.readLines())
    }
}