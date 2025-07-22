// encoding: UTF-8
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
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.view.beans.FacesMessages;

public class AbstractController {

	private static final Logger LOGGER = LogManager.getLogger(AbstractController.class);

	public static final String TYPE_HTML = "text/html";

	public static final String TYPE_PDF = "application/pdf";

	public static final String TYPE_TXT = "application/txt";

	public static final String TYPE_DOC = "application/vnd.ms-word";

	public static final String TYPE_RTF = "application/rtf";

	public static final String LOGOMARCA_JPG = "logomarca.jpg";

	public static final String SUBJECT_MESSAGE_EMAIL = "Documentos para importa��o no sistema CSO";

	public static final String FILE_TYPE_TXT = ".txt";

	public static final String FILE_TYPE_RTF = ".rtf";

	public static final String FILE_TYPE_PDF = ".pdf";

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

}
