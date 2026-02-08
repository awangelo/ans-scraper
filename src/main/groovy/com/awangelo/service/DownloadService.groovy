package com.awangelo.service

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download

import static com.awangelo.service.CrawlerService.USER_AGENT

final class DownloadService {
    private static final String DIRETORIO_DOWNLOADS = './Downloads'

    static void prepararDiretorio() {
        def dir = new File(DIRETORIO_DOWNLOADS)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    static void baixarStream(String url, String nomeArquivo) {
        if (!url) return

        File arquivoDestino = new File(DIRETORIO_DOWNLOADS, nomeArquivo)

        try {
            println "   -> Baixando: ${nomeArquivo}..."

            HttpBuilder.configure {
                request.uri = url
                request.headers['User-Agent'] = USER_AGENT
            }.get {
                Download.toFile(delegate, arquivoDestino)
            }

            println "   -> Arquivo salvo em ${arquivoDestino.absolutePath}"

        } catch (Exception e) {
            println "   -> Erro ao baixar ${nomeArquivo}: ${e.message}"
            if (arquivoDestino.exists() && arquivoDestino.length() == 0) {
                arquivoDestino.delete()
            }
        }
    }

    static void salvarCsvHistorico(List<Map<String, String>> dados) {
        if (!dados || dados.isEmpty()) return

        File arquivo = new File(DIRETORIO_DOWNLOADS, 'historico_versoes.csv')

        try {
            arquivo.withWriter('UTF-8') { writer ->
                writer.writeLine('Competencia;Publicacao;InicioVigencia')
                dados.each { linha ->
                    writer.writeLine("${linha.competencia};${linha.publicacao};${linha.vigencia}")
                }
            }
            println '   -> Sucesso: CSV gerado.'
        } catch (Exception e) {
            println "   -> Erro ao salvar CSV: ${e.message}"
        }
    }
}
