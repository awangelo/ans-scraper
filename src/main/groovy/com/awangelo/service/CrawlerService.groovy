package com.awangelo.service

import com.awangelo.parser.AnsHtmlParser
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import static groovyx.net.http.HttpBuilder.configure

final class CrawlerService {
    private final HttpBuilder httpBuilder
    private static final String BASE_URL = 'https://www.gov.br/ans/pt-br'

    CrawlerService() {
        this.httpBuilder = configure {
            request.uri = BASE_URL
            request.headers['User-Agent'] = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36'
        }
    }

    Object buscarDocumentosTiss() {
        println '1. Buscando página inicial...'
        Document home = fetchPage()

        println '2. Buscando URL Espaço do Prestador...'
        String urlPrestador = AnsHtmlParser.extrairLinkPrestador(home)

        println '3. Buscando TISS...'
        Document docPrestador = fetchPage(urlPrestador)
        String urlTiss = AnsHtmlParser.extrairLinkTiss(docPrestador)

        println '4.1. Buscando Versão Atual...'
        Document docTiss = fetchPage(urlTiss)
        String urlVersao = AnsHtmlParser.extrairLinkVersaoAtual(docTiss)

        println '4.2. Buscando Histórico das versões...'
        Document docHist = fetchPage(urlTiss)
        String urlHist = AnsHtmlParser.extrairLinkHistorico(docHist)

        println '4.3. Buscando Tabelas Relacionadas...'
        Document docRel = fetchPage(urlTiss)
        String urlRel = AnsHtmlParser.extrairLinkTabelas(docRel)

        println '5. Buscando Componente de Comunicação...'
        Document docVersao = fetchPage(urlVersao)
        String urlDownload = AnsHtmlParser.extrairLinkComponenteComunicacao(docVersao)

        urlDownload
    }

    private Document fetchPage() {
        fetchPage(BASE_URL)
    }

    private Document fetchPage(String url) {
        try {
            httpBuilder.get {
                request.uri = url

                response.success { _, def body ->
                    if (body instanceof Document) {
                        body
                    }

                    Jsoup.parse(body.toString(), url)
                }

                // status >= 400
                response.failure { FromServer fs, Object body ->
                    throw new RuntimeException("Falha HTTP ${fs.statusCode} ao acessar ${url}")
                }

                // "This is different from a failure condition because there is no response, no status code, no headers, etc"
                response.exception { t ->
                    throw new RuntimeException("Erro de conexão ao acessar ${url}: ${t.message}", t)
                }
            } as Document
        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar página: ${url}", e)
        }
    }
}