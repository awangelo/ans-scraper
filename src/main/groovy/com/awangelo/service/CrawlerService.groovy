package com.awangelo.service

import groovyx.net.http.HttpBuilder
import org.jsoup.nodes.Document

import static groovyx.net.http.HttpBuilder.configure

final class CrawlerService {
    private final HttpBuilder httpBuilder

    CrawlerService() {
        this.httpBuilder = configure {
            request.uri = 'https://www.gov.br/ans/pt-br'
            request.headers['User-Agent'] = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36'
        }
    }

    Object buscarDocumentosTiss() {
        fetchPage()
    }

    // Busca a pagina base definida em request.uri
    private Document fetchPage() {
        httpBuilder.get() as Document
    }

    private Document fetchPage(String url) {
//
    }
}