/**
 * Pokemon Go Bot  Copyright (C) 2016  PokemonGoBot-authors (see authors.md for more information)
 * This program comes with ABSOLUTELY NO WARRANTY;
 * This is free software, and you are welcome to redistribute it under certain conditions.
 *
 * For more information, refer to the LICENSE file in this repositories root directory
 */

package ink.abb.pogo.scraper.tasks

import ink.abb.pogo.scraper.util.Log
import ink.abb.pogo.scraper.Bot
import ink.abb.pogo.scraper.Context
import ink.abb.pogo.scraper.Settings
import ink.abb.pogo.scraper.Task

/**
 * @author Michael Meehan (Javapt)
 */

class EvolvePokemon : Task {
    override fun run(bot: Bot, ctx: Context, settings: Settings) {
        if (settings.autoEvolve.isEmpty()) {
            return
        }
        val groupedPokemon = ctx.api.inventories.pokebank.pokemons.groupBy { it.pokemonId }
        val canEvolve = groupedPokemon.filter {
            val candyNeeded = settings.candyRequiredByPokemon[it.key.number]
            candyNeeded != null && candyNeeded > 0 && it.value.first().candy >= candyNeeded
        }
        if (!canEvolve.isEmpty()) {
            canEvolve.forEach {
                val sorted = it.value.sortedByDescending { it.cp }
                val candyNeeded = settings.candyRequiredByPokemon[it.key.number]
                if (candyNeeded != null) {
                    for ((index, pokemon) in sorted.withIndex()) {
                        if (pokemon.candy < candyNeeded) {
                            break
                        }
                        if(index == 0) {
                            continue
                        }
                        Log.green("Evolving ${pokemon.pokemonId.name} because we have ${pokemon.candy} candy and only need ${candyNeeded}.")
                        pokemon.evolve()
                    }
                }
            }
        }
    }
}