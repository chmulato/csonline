// Arquivo salvo em UTF-8
// Certifique-se que o editor está configurado para UTF-8
package br.com.mulato.cso.dry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.mulato.cso.exception.ReportException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.view.beans.FacesMessages;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

public class AbstractController {

	private static final Logger LOGGER = LogManager.getLogger(AbstractController.class);

	public final String TYPE_HTML = "text/html";

	public final String TYPE_PDF = "application/pdf";

	public final String TYPE_TXT = "application/txt";

	public final String TYPE_DOC = "application/vnd.ms-word";

	public final String TYPE_RTF = "application/rtf";

	private final JRExporter exporterPDF = new JRPdfExporter();

	private final JRExporter exporterRTF = new JRRtfExporter();

	private final JRHtmlExporter exporterHTML = new JRHtmlExporter();

	public final String LOGOMARCA_JPG = "logomarca.jpg";

	public final String SUBJECT_MESSAGE_EMAIL = "Documentos para importa��o no sistema CSO";

	public final String FILE_TYPE_TXT = ".txt";

	public final String FILE_TYPE_RTF = ".rtf";

	public final String FILE_TYPE_PDF = ".pdf";

	public AbstractController() {
		super();
	}

	public String goToPage(final String navigation) {
		return navigation + "?faces-redirect=true";
	}

	public String goToBackPage(final String navigation) {
		return navigation;
	}

	@SuppressWarnings("rawtypes")
	protected String getParameter(final String parameterId) throws WebException {
		final String msg = "Par�metro de navega��o inv�lido! ";
		String value = null;
		try {
			final Map requestMap = getExternalContext().getRequestParameterMap();
			value = (String) requestMap.get(parameterId);
		} catch (final Exception e) {
			LOGGER.error(msg + e.getMessage());
			throw new WebException(msg);
		}
		return value;
	}

	protected String getTempPath() {
		final ServletContext sc = (ServletContext) getExternalContext().getContext();
		return sc.getRealPath("WEB-INF" + "/" + "tmp");
	}

	public void criarLogomarca(final byte[] bytes) {
		LOGGER.info("Salvando logomarca.JPG na pasta tmp do servidor ...");
		if (bytes != null) {
			final File image = new File(getTempPath() + "/" + LOGOMARCA_JPG);
			try {
				final FileOutputStream fos = new FileOutputStream(image);
				final byte[] buffer = new byte[256];
				final InputStream is = new ByteArrayInputStream(bytes);

				while (is.read(buffer) > 0) {
					fos.write(buffer);
				}

				fos.close();

			} catch (final FileNotFoundException e) {
				LOGGER.error("Erro p/ salvar logomarca no servidor. " + e.getMessage());
			} catch (final IOException e) {
				LOGGER.error("Erro p/ salvar logomarca no servidor. " + e.getMessage());
			}
		}
	}

	public void exportReportPDF(final JasperPrint jasperPrint) throws ReportException {
		final String msg = "Erro ao exportar o relat�rio para o usu�rio: ";
		try {
			final HttpServletResponse response = (HttpServletResponse) getExternalContext().getResponse();
			response.setContentType(TYPE_PDF);
			exporterPDF.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporterPDF.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
			exporterPDF.exportReport();
			getFacesCurrentInstance().responseComplete();
		} catch (final RuntimeException e) {
			LOGGER.error(msg + e.getMessage());
			throw new ReportException("N�o foi poss��vel montar seu relat�rio. Tente mais tarde.");
		} catch (IOException | JRException e) {
			LOGGER.error(msg + e.getMessage());
			throw new ReportException("N�o foi poss��vel montar seu relat�rio. Tente mais tarde.");
		}

	}

	public void exportReportRTF(final JasperPrint jasperPrint) throws ReportException {
		final String msg = "Erro ao exportar o relat�rio para o usu�rio: ";
		try {
			final HttpServletResponse response = (HttpServletResponse) getExternalContext().getResponse();
			response.setContentType(TYPE_RTF);
			response.addHeader("Content-disposition", "attachment; filename=relatorioWord.rtf");
			exporterRTF.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporterRTF.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
			exporterRTF.exportReport();
			getFacesCurrentInstance().responseComplete();
		} catch (final RuntimeException e) {
			LOGGER.error(msg + e.getMessage());
			throw new ReportException("N�o foi poss��vel montar seu relat�rio. Tente mais tarde.");
		} catch (IOException | JRException e) {
			LOGGER.error(msg + e.getMessage());
			throw new ReportException("N�o foi poss��vel montar seu relat�rio. Tente mais tarde.");
		}

	}

	public void exportReportHTML(final JasperPrint jasperPrint) throws ReportException {
		final String msg = "Erro ao exportar relat�rio: ";
		try {
			final PrintWriter printWriter = getResponse().getWriter();
			getResponse().setContentType(TYPE_HTML);
			getResponse().setCharacterEncoding("ISO-8859-1");
			getRequest().getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
			exporterHTML.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporterHTML.setParameter(JRExporterParameter.OUTPUT_WRITER, printWriter);
			exporterHTML.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
			exporterHTML.exportReport();
		} catch (IOException | JRException e) {
			LOGGER.error(msg + e.getMessage());
			throw new ReportException("N�o foi poss��vel montar seu relat�rio. Tente mais tarde.");
		}
	}

	protected void downloadArquivo(final String filename, final String fileType) {
		String msg = "Erro ao realizar download do arquivo tempor�rio ";
		try {
			final File file = new File(getTempPath() + "/" + filename);
			final HttpServletResponse response = getResponse();
			response.setContentType(fileType);
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			final InputStream in = new FileInputStream(file);
			final PrintWriter output = response.getWriter();
			int bit = 256;
			try {
				while ((bit) >= 0) {
					bit = in.read();
					output.write(bit);
				}
			} catch (final IOException e) {
				LOGGER.error(msg + filename + ": " + e.getMessage());
			}
			output.flush();
			output.close();
			in.close();
			// remove arquivo do servidor Tomcat
			file.delete();
			getFacesCurrentInstance().responseComplete();
		} catch (final FileNotFoundException e) {
			msg = "Erro ao realizar download do arquivo tempor�rio " + filename + ": ";
			LOGGER.error(msg + e.getMessage());
			FacesMessages.mensErro("Por favor, contate o suporte. Houve um problema ao gerar o termo solicitado.");
		} catch (final IOException e) {
			msg = "Erro ao realizar download do arquivo temporario " + filename + ": ";
			LOGGER.error(msg + e.getMessage());
			FacesMessages.mensErro("Por favor, contate o suporte. Houve um problema ao gerar o termo solicitado.");
		}
	}

	protected FacesContext getFacesCurrentInstance() {
		return FacesContext.getCurrentInstance();
	}

	protected ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}

	protected HttpServletResponse getResponse() {
		return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}

	protected HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	public JRExporter getExporterPDF() {
		return exporterPDF;
	}

	public JRExporter getExporterRTF() {
		return exporterRTF;
	}

	public JRHtmlExporter getExporterHTML() {
		return exporterHTML;
	}
}
