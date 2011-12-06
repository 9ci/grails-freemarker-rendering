package grails.plugin.freemarker

import org.springframework.web.servlet.View

import freemarker.template.Configuration
import freemarker.template.SimpleHash
import freemarker.template.Template

import org.apache.commons.io.output.StringBuilderWriter
import org.xhtmlrenderer.resource.XMLResource
import grails.plugin.rendering.document.XmlParseException
import grails.util.GrailsUtil
import org.w3c.dom.Document
import org.xml.sax.InputSource
import javax.servlet.http.HttpServletResponse
import grails.plugin.rendering.pdf.PdfRenderingService

class FreemarkerPdfService extends PdfRenderingService {
	static transactional = false

	def freemarkerViewService
	//def grailsApplication

	/**
	 * Complete ovverride of super to get xhtml doc from freemarker view
	 * args can be
	 * view     -> the name of the freemarker template
	 * model    -> the model to pass to freemarker for rendering
	 * plugin   -> the plugin where the template is located
	 * filename -> the name of the pdf file used in the header Content-Disposition", "attachment; filename=X sent back to the browser
	 * document -> the org.w3c.dom.Document if you already have one.
	 * bytes    -> if you already have a byte array for rendering to pdf
	 * input    -> ??
	 * stream   -> ??
	 */
	@Override
	OutputStream render(Map args, OutputStream outputStream = new ByteArrayOutputStream()){
		
		Document document = args.document
		if(!document){
			if (!args.view) throw new IllegalArgumentException("The 'view' argument must be specified")
			
			String xhtml = renderString(args.view, args.model, args.plugin)
			document = createXmlDocument(xhtml)
		}
		
		render(args, document, outputStream)
	}
	
	@Override
	OutputStream render(Map args, Document document, OutputStream outputStream = new ByteArrayOutputStream()) {
		super.render( args,  document,  outputStream )
	}
	
	@Override // so we have logging and can see whats happening
	boolean render(Map args, HttpServletResponse response) {
		super.render( args, response)
	}

    String renderString(String viewName , Map model, String pluginName = null){
		def view = freemarkerViewService.getView(viewName, pluginName)
		if (!view) throw new IllegalArgumentException("The 'view' ${viewName} ${ pluginName?('for plugin: ' + pluginName):''} cannot be found")
        return freemarkerViewService.renderString(view,  model)
    }

	
	@Override //just so we can do more logging
	protected doRender(Map args, Document document, OutputStream outputStream){
		super.doRender( args,  document,  outputStream)
	}
	
	/**
	 * adds additional argument processing, like setting default filename, 
	 */
	@Override
	protected processArgs(Map args) {
		super.processArgs(args)
		args.filename = args.filename ?: "${args.view}.pdf"
		//TODO nail more with the serverUrl
	}

	String generateXhtml(String viewName, Map model, String pluginName = null) {
		View view
		def xhtmlWriter = new StringBuilderWriter() 

		//if getRequestAttributes() is null(false) then create a mock request, etc with RenderEnvironment
		if(RequestContextHolder.getRequestAttributes()){
			renderXhtmlDocument( xhtmlWriter,  viewName,  model, pluginName)
		}else{
			RenderEnvironment.with(grailsApplication.mainContext, xhtmlWriter) {
				renderXhtmlDocument( xhtmlWriter,  viewName,  model, pluginName)
			}
		}
		
		def xhtml = xhtmlWriter.toString()
		xhtmlWriter.close()

		if(log.debugEnabled) log.debug("xhtml for $args -- \n ${xhtml}")
		
		return xhtml
	}
	
	void renderXhtmlDocument(Writer xhtmlWriter, String viewName, Map model, String pluginName = null){
		if (pluginName) {
			view = freemarkerViewResolver.getView(viewName,pluginName)
		}else{
			view = freemarkerViewResolver.getView(viewName)
		}
		view.renderToWriter(model, xhtmlWriter)	
	}
	
	Document createXmlDocument(String xhtml) {
		try {
			XMLResource.load(new InputSource(new StringReader(xhtml))).document
		} catch (Exception e) {
			if (log.errorEnabled) {
				GrailsUtil.deepSanitize(e)
				log.error("xml parse exception for input source: $xhtml", e)
			}
			throw new XmlParseException(xhtml, e)
		}
	}
	
}

