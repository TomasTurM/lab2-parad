// =====================================================================
// Ejercicio 6: Integración del sistema completo
// =====================================================================

object Main {
  def main(args: Array[String]): Unit = {

    // ------------------------------------------------------------------
    // Paso 1: Cargar diccionarios
    // ------------------------------------------------------------------
    val dictionary: List[NamedEntity] = Dictionary.loadAll()

    println(s"Diccionario cargado: ${dictionary.size} entidades.\n")

    // ------------------------------------------------------------------
    // Paso 2: Descargar posts
    // ------------------------------------------------------------------
    val subscriptions = FileIO.readSubscriptions()

    val allPosts: List[(String, List[String])] = subscriptions.map { url =>
      println(s"Descargando posts de: $url")
      val json   = FileIO.downloadFeed(url)
      val titles = FileIO.extractPostTitles(json)
      (url, titles)
    }

    // ------------------------------------------------------------------
    // Paso 3: Detectar entidades y mostrar resultados por post
    // ------------------------------------------------------------------
    allPosts.foreach{ case (_, titles) => 
      titles.foreach{ title =>
        val entities = Analyzer.detectEntities(title, dictionary)
        val res = Formatters.formatNERResult(title, entities)

        println(res)
      }
    }

    // ------------------------------------------------------------------
    // Paso 4: Estadísticas globales
    // ------------------------------------------------------------------
    val entities = allPosts.flatMap{ case (_, titles) => 
      titles.flatMap{ title =>
        Analyzer.detectEntities(title, dictionary)
      }
    }

    val counts = Analyzer.countByType(entities)
    val stats = Formatters.formatEntityStats(counts)
    
    println(stats)
  }
}
