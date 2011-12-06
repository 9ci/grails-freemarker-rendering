package grails.plugin.freemarker

import grails.test.*
import org.apache.pdfbox.util.PDFTextStripper
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument

import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.springframework.mock.web.MockHttpServletResponse
import grails.plugin.rendering.document.*

class FreemarkerPdfServiceTests extends GroovyTestCase {
    def freemarkerPdfService
	def freemarkerViewService
    
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetView() {
        def view = freemarkerViewService.getView("simple")
        assertNotNull view //.getTemplate(Locale.US)
        
    }
    
	def test_simpleRender() {
		def lines = extractTextLines(getSimpleTemplate())
		assert lines[0] == 'This is a PDF!'
		assert lines[1] == 'gogogo'
	}
	
	def test_renderToResponse() {
		def response = createMockResponse()
		freemarkerPdfService.render(getSimpleTemplate(), response)
		assert response.contentAsByteArray.size() > 0
		assert response.contentType == "application/pdf"
		assert response.getHeader("Content-Disposition") == "attachment; filename=\"/simple.pdf\";"
	}
	
	def test_NoTemplateThrowsException() {
		try{
			freemarkerPdfService.render([:])
			fail "should not have made it here"
		}catch(e){
			assert e instanceof IllegalArgumentException
		}
	}
	
	def test_UnknownTemplateThrowsException() {
		try{
			freemarkerPdfService.render([view: '/xxx', model: [var: 'gogogo']])
			fail "should not have made it here"
		}catch(e){
			assert e instanceof IllegalArgumentException
		}
	}
	
	
	/*
	def renderTemplateInPlugin() {
		when:
		renderer.render(pluginTemplate)
		then:
		notThrown(Exception)
	}
	
	def renderWithNoTemplateThrowsException() {
		when:
		renderer.render([:])
		then:
		thrown(IllegalArgumentException)
	}

	def renderWithUnknownTemplateThrowsException() {
		when:
		renderer.render(template: "/asdfasdfasd")
		then:
		thrown(UnknownTemplateException)
	}
	

	def renderToResponse() {
		given:
		def response = createMockResponse()
		when:
		renderer.render(simpleTemplate, response)
		then:
		response.contentAsByteArray.size() > 0
	}

	def renderToResponseViaBytes() {
		given:
		def response = createMockResponse()
		when:
		def bytes = renderer.render(simpleTemplate).toByteArray()
		def args = simpleTemplate.clone()
		args.remove('template')
		args.bytes = bytes
		renderer.render(args, response)
		then:
		response.contentAsByteArray.size() > 0
	}

	def badXmlThrowsXmlParseException() {
		when:
		renderer.render(template: "/bad-xml")
		then:
		thrown(XmlParseException)
	}

	def "can handle data uris"() {
		when:
		renderer.render(dataUriTemplate)
		then:
		notThrown(Exception)
	}
	*/
	
	protected extractTextLines(Map renderArgs) {
		extractTextLines(createPdf(renderArgs))
	}
	
	protected extractTextLines(byte[] bytes) {
		extractTextLines(createPdf(new ByteArrayInputStream(bytes)))
	}
	
	protected extractTextLines(PDDocument pdf) {
		protected lines = new PDFTextStripper().getText(pdf).readLines()
		pdf.close()
		lines
	}

	protected createPdf(Map renderArgs) {
		def inStream = new PipedInputStream()
		def outStream = new PipedOutputStream(inStream)
		freemarkerPdfService.render(renderArgs, outStream)
		outStream.close()
		createPdf(inStream)
	}
	
	protected createPdf(InputStream inputStream) {
		def parser = new PDFParser(inputStream)
		parser.parse()
		parser.getPDDocument()
	}
	
	protected createController() {
		grailsApplication.mainContext['RenderingController']
	}
	
	protected getSimpleTemplate(Map args = [:]) {
		[view: '/simple', model: [var: 'gogogo']] + args
	}

	protected getPluginTemplate(Map args = [:]) {
		[template: '/plugin-pdf', plugin: 'pdf-plugin-test', model: [var: 1]] + args
	}

	protected getDataUriTemplate() {
		[template: '/datauri']
	}

	protected createMockResponse() {
		new MockHttpServletResponse()
	}


}
