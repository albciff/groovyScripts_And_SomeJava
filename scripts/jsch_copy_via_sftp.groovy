@Grab(group='com.jcraft', module='jsch', version='0.1.53')

import com.jcraft.jsch.*

java.util.Properties config = new java.util.Properties()
config.put "StrictHostKeyChecking", "no"

JSch ssh = new JSch()
Session sess = ssh.getSession "user", "host", 22

sess.with {
    setConfig config
    setPassword "XXXXXXXXXXXXXXX"
    connect()
    Channel chan = openChannel "sftp"
    chan.connect()
    ChannelSftp sftp = (ChannelSftp) chan;
    def sessionsFile = new File('C:/temp/someFile.pdf')
    sessionsFile.withInputStream { istream -> sftp.put(istream, "/tmp/someFile") }
    chan.disconnect()
    disconnect()
}
