package gov.nih.ncats.molwitch.renderer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class AttachmentInfo {
    private List<String> attachments = new ArrayList<String>();
    private List<Integer> attachmentLOC = new ArrayList<Integer>();
    private List<Float> attachmentSIZE = new ArrayList<Float>();
    private List<ARGBColor> attachmentCOL = new ArrayList<>();
}
