package net.noerlol.neotrans.gui;

import net.noerlol.neotrans.Main;
import net.noerlol.neotrans.project.ProjectConfig;
import net.noerlol.neotrans.utils.PlatformSpecific;
import net.noerlol.neotrans.utils.Version;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class NeoGUI extends JFrame {
    private final NeoTextArea glVersion = new NeoTextArea();
    private final NeoTextArea tab_length = new NeoTextArea();
    private final NeoTextArea source_directory = new NeoTextArea();
    private final NeoTextArea name = new NeoTextArea();
    private final NeoTextArea version = new NeoTextArea();
    private final NeoTextArea author = new NeoTextArea();
    private final NeoTextArea github = new NeoTextArea();
    private NeoGUIConsole console = null;

    private final ProjectConfig projectConfig = new ProjectConfig();
    public NeoGUI() {
        super("NeoTranspiler");
        super.setSize(new Dimension(640, 360));
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLayout(new BorderLayout());

        JPanel panel_ProjectConfig = new JPanel();
        panel_ProjectConfig.add(glVersion);
        glVersion.setText(Version.VERSION);
        glVersion.setToolTipText("GlVersion");
        panel_ProjectConfig.add(tab_length);
        tab_length.setText("tab length");
        tab_length.setToolTipText("tab length");
        panel_ProjectConfig.add(source_directory);
        source_directory.setText("project source directory");
        source_directory.setToolTipText("project source directory");
        panel_ProjectConfig.add(name);
        name.setText("project name");
        name.setToolTipText("project name");
        panel_ProjectConfig.add(version);
        version.setText("project version");
        version.setToolTipText("project version");
        panel_ProjectConfig.add(version);
        author.setText("project author");
        author.setToolTipText("project author");
        panel_ProjectConfig.add(author);
        github.setText("project github");
        github.setToolTipText("project github");
        panel_ProjectConfig.add(github);

        panel_ProjectConfig.setLayout(new GridLayout(3, 3, 10 ,10));

        if (Main.args.isEnabled("Gpath", true)) {
            projectConfig.loadConfig(new File(Main.args.getOption("Gpath").getValue()));
            glVersion.setText(projectConfig.getString("version"));
            tab_length.setText(String.valueOf(projectConfig.getInteger("project.tab_length")));
            source_directory.setText(projectConfig.getString("project.source_directory"));
            name.setText(projectConfig.getString("project.name"));
            version.setText(projectConfig.getString("project.version"));
            author.setText(projectConfig.getString("project.author"));
            github.setText(projectConfig.getString("project.github_repository")); // Todo: add text above the textbox
        }

        JPanel consoleLogRunButtons = new JPanel();
        consoleLogRunButtons.setMaximumSize(new Dimension(400, 600));
        consoleLogRunButtons.setMinimumSize(new Dimension(400, 600));
        consoleLogRunButtons.setSize(new Dimension(400, 600));
        consoleLogRunButtons.setLayout(new BorderLayout());
        JButton run = new JButton();
        run.setText("Run");

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (console == null || console.isCanCreateNew()) {
                    console = new NeoGUIConsole();
                }
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "-cp", "lib" + File.separator + "libjda" + Version.libjda_VERSION + ".jar" + PlatformSpecific.CLASSPATH_SEPARATOR + "lib" + File.separator + "libstd" + Version.libstd_VERSION + ".jar" + PlatformSpecific.CLASSPATH_SEPARATOR + "build" + File.separator + "compiled.jar",  "src.Main");
                    handleProcessOutput(pb.start());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        consoleLogRunButtons.add(run, BorderLayout.NORTH);

        JPanel projectCreation = new JPanel();
        projectCreation.setLayout(new BorderLayout());
        projectCreation.setFocusable(false);
        JButton createProject = new JButton();
        createProject.setText("Create Project");
        createProject.setFocusable(false);

        JButton loadProject = new JButton();
        loadProject.setText("Load Project");
        loadProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("YML or YAML files", "yml", "yaml");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(projectCreation);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    projectConfig.loadConfig(chooser.getSelectedFile().getAbsoluteFile());
                    glVersion.setText(projectConfig.getString("version"));
                    tab_length.setText(String.valueOf(projectConfig.getInteger("project.tab_length")));
                    source_directory.setText(projectConfig.getString("project.source_directory"));
                    name.setText(projectConfig.getString("project.name"));
                    version.setText(projectConfig.getString("project.version"));
                    author.setText(projectConfig.getString("project.author"));
                    github.setText(projectConfig.getString("project.github_repository"));
                }
            }
        });

        projectCreation.add(createProject, BorderLayout.WEST);
        projectCreation.add(loadProject, BorderLayout.EAST);

        super.add(projectCreation, BorderLayout.NORTH);
        super.add(panel_ProjectConfig, BorderLayout.CENTER);
        super.add(consoleLogRunButtons, BorderLayout.SOUTH);
        super.setVisible(true);
    }

    private void handleProcessOutput(Process process) throws IOException {
        // Capture the output from the process
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // Redirect the output to your custom function
            console.console.appendText(line);
        }
    }
}
